package data.remote.ratelimit

import com.google.common.truth.Truth.assertThat
import data.exception.InvalidBucketCapacityException
import data.exception.InvalidRefillRateException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val NANOS_PER_MILLI = 1_000_000L

class RateLimiterTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.limiter(capacity: Int = 10, refillPerSecond: Double = 5.0) =
        RateLimiter(capacity, refillPerSecond, nanoTime = { currentTime * NANOS_PER_MILLI })

    private fun RateLimiter.countAvailableTokens(): Int {
        var count = 0
        while (tryAcquire()) count++
        return count
    }

    private fun RateLimiter.drain() {
        countAvailableTokens()
    }


    @Test
    fun `given zero capacity when creating limiter then throws InvalidBucketCapacityException`() {
        assertThrows<InvalidBucketCapacityException> { RateLimiter(0, 5.0) }
    }

    @Test
    fun `given zero refill rate when creating limiter then throws InvalidRefillRateException`() {
        assertThrows<InvalidRefillRateException> { RateLimiter(10, 0.0) }
    }

    @Test
    fun `given negative refill rate when creating limiter then throws InvalidRefillRateException`() {
        assertThrows<InvalidRefillRateException> { RateLimiter(10, -1.0) }
    }


    @Test
    fun `given new limiter when trying capacity times then all succeed and the next fails`() = runTest {
        // Given
        val limiter = limiter(capacity = 10)

        // When
        val results = List(11) { limiter.tryAcquire() }

        // Then
        assertThat(results.take(10)).containsExactlyElementsIn(List(10) { true })
        assertThat(results.last()).isFalse()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given empty bucket when trying to acquire then returns false without waiting`() = runTest {
        // Given
        val limiter = limiter()
        limiter.drain()
        val before = currentTime

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertThat(acquired).isFalse()
        assertThat(currentTime).isEqualTo(before)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given empty bucket when 200ms pass at 5 per second then exactly one token is refilled`() = runTest {
        // Given
        val limiter = limiter(refillPerSecond = 5.0)
        limiter.drain()

        // When
        advanceTimeBy(200.milliseconds)

        // Then
        assertThat(limiter.countAvailableTokens()).isEqualTo(1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given partial refills when time passes in small steps then fractions of a token are not lost`() = runTest {
        // Given
        val limiter = limiter(refillPerSecond = 5.0)
        limiter.drain()

        advanceTimeBy(100.milliseconds)
        val afterFirstStep = limiter.tryAcquire()
        advanceTimeBy(100.milliseconds)
        val afterSecondStep = limiter.tryAcquire()

        // Then
        assertThat(afterFirstStep).isFalse()
        assertThat(afterSecondStep).isTrue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given long idle time when counting tokens then they never exceed the capacity`() = runTest {
        // Given
        val limiter = limiter(capacity = 10, refillPerSecond = 5.0)
        limiter.drain()

        // When
        advanceTimeBy(60_000.milliseconds)

        // Then
        assertThat(limiter.countAvailableTokens()).isEqualTo(10)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given available token when acquiring then returns immediately`() = runTest {
        // Given
        val limiter = limiter()
        val before = currentTime

        // When
        limiter.acquire()

        // Then
        assertThat(currentTime).isEqualTo(before)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given empty bucket when acquiring then waits exactly until one token is refilled`() = runTest {
        // Given
        val limiter = limiter(refillPerSecond = 5.0)
        limiter.drain()
        val before = currentTime

        // When
        limiter.acquire()

        // Then
        assertThat(currentTime - before).isEqualTo(200)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given waiting acquire when caller cancels then it stops and does not take a token`() = runTest {
        // Given
        val limiter = limiter(refillPerSecond = 5.0)
        limiter.drain()
        val job = launch { limiter.acquire() }
        runCurrent()

        // When
        job.cancel()
        job.join()
        advanceTimeBy(200.milliseconds)

        // Then
        assertThat(job.isCancelled).isTrue()
        assertThat(limiter.countAvailableTokens()).isEqualTo(1)
    }


    @Test
    fun `given 100 simultaneous threads when bucket holds 10 tokens then exactly 10 succeed`() {
        // Given:
        val limiter = RateLimiter(maxBucketCapacity = 10, refillRatePerSecond = 5.0, nanoTime = { 0L })
        val threads = 100
        val executor = Executors.newFixedThreadPool(threads)
        val startGate = CountDownLatch(1)
        val done = CountDownLatch(threads)
        val successes = AtomicInteger()

        // When
        repeat(threads) {
            executor.execute {
                startGate.await()
                if (limiter.tryAcquire()) successes.incrementAndGet()
                done.countDown()
            }
        }
        startGate.countDown()
        val finished = done.await(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        // Then
        assertThat(finished).isTrue()
        assertThat(successes.get()).isEqualTo(10)
    }

    @Test
    fun `given many concurrent acquires when tokens keep refilling then all complete without deadlock`() =
        runBlocking {
            // Given
            val limiter = RateLimiter(maxBucketCapacity = 5, refillRatePerSecond = 1000.0)

            // When
            val results = withTimeout(10.seconds) {
                List(50) { async(Dispatchers.Default) { limiter.acquire() } }.awaitAll()
            }

            // Then
            assertThat(results).hasSize(50)
        }
}
