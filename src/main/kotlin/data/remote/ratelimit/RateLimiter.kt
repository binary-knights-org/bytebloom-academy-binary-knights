package data.remote.ratelimit

import data.exception.InvalidBucketCapacityException
import data.exception.InvalidRefillRateException
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil
import kotlin.time.Duration.Companion.nanoseconds

class RateLimiter(
    private val maxBucketCapacity: Int,
    private val refillRatePerSecond: Double,
    private val nanoTime: () -> Long = System::nanoTime
) {
    init {
        if (maxBucketCapacity <= NO_CAPACITY) throw InvalidBucketCapacityException()
        if (refillRatePerSecond <= NO_REFILL_RATE || !refillRatePerSecond.isFinite()) {
            throw InvalidRefillRateException()
        }
    }

    private class BucketState(val tokens: Double, val lastRefillNanos: Long)

    private val state = AtomicReference(BucketState(maxBucketCapacity.toDouble(), nanoTime()))

    fun tryAcquire(): Boolean = consumeOrGetWaitNanos() == NO_WAIT_NANOS

    suspend fun acquire() {
        while (true) {
            val waitNanos = consumeOrGetWaitNanos()
            if (waitNanos == NO_WAIT_NANOS) return
            delay(waitNanos.nanoseconds)
        }
    }

    private fun consumeOrGetWaitNanos(): Long {
        while (true) {
            val current = state.get()
            val now = maxOf(nanoTime(), current.lastRefillNanos)
            val refilled = minOf(
                maxBucketCapacity.toDouble(),
                current.tokens + (now - current.lastRefillNanos) * refillRatePerSecond / NANOS_PER_SECOND
            )

            if (refilled < TOKEN_COST) {
                val missingTokens = TOKEN_COST - refilled
                return ceil(missingTokens / refillRatePerSecond * NANOS_PER_SECOND)
                    .toLong()
                    .coerceAtLeast(MIN_WAIT_NANOS)
            }

            if (state.compareAndSet(current, BucketState(refilled - TOKEN_COST, now)))
                return NO_WAIT_NANOS
        }
    }

    private companion object {
        const val NANOS_PER_SECOND = 1_000_000_000.0
        const val TOKEN_COST = 1.0
        const val NO_CAPACITY = 0
        const val NO_REFILL_RATE = 0.0
        const val NO_WAIT_NANOS = 0L
        const val MIN_WAIT_NANOS = 1L
    }
}
