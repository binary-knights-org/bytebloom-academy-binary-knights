package data.remote.circuitbreaker

import com.google.common.truth.Truth.assertThat
import data.exception.CallNotPermittedException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class CircuitBreakerTest {

    private class TestClock(var currentTimeMs: Long = 0L) {
        fun advanceTime(millis: Long) {
            currentTimeMs += millis
        }
    }

    private fun createCircuitBreaker(
        failureThreshold: Int = 5,
        resetTimeoutMillis: Long = 5000L,
        clock: TestClock = TestClock()
    ): Pair<CircuitBreaker, TestClock> {
        val breaker = CircuitBreaker(
            failureThreshold = failureThreshold,
            resetTimeoutMillis = resetTimeoutMillis,
            timeProvider = { clock.currentTimeMs }
        )
        return breaker to clock
    }


    @Test
    fun `given new circuit breaker then initial state is CLOSED and failures are zero`() {
        val (breaker, _) = createCircuitBreaker()

        assertThat(breaker.state).isEqualTo(CircuitBreakerState.CLOSED)
        assertThat(breaker.consecutiveFailures).isEqualTo(0)
    }

    @Test
    fun `given invalid threshold when constructing then throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            CircuitBreaker(failureThreshold = 0)
        }
    }

    @Test
    fun `given invalid reset timeout when constructing then throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            CircuitBreaker(resetTimeoutMillis = 0L)
        }
    }


    @Test
    fun `given CLOSED state when call succeeds then result is returned and failures remain zero`() {
        val (breaker, _) = createCircuitBreaker()

        val result = breaker.execute { "success" }

        assertThat(result).isEqualTo("success")
        assertThat(breaker.state).isEqualTo(CircuitBreakerState.CLOSED)
        assertThat(breaker.consecutiveFailures).isEqualTo(0)
    }

    @Test
    fun `given CLOSED state with consecutive failures when call succeeds then failure counter is reset to zero`() {
        val (breaker, _) = createCircuitBreaker(failureThreshold = 5)

        repeat(4) {
            assertThrows<IOException> {
                breaker.execute { throw IOException("Remote network error") }
            }
        }
        assertThat(breaker.consecutiveFailures).isEqualTo(4)
        assertThat(breaker.state).isEqualTo(CircuitBreakerState.CLOSED)

        val result = breaker.execute { "recovered" }

        assertThat(result).isEqualTo("recovered")
        assertThat(breaker.consecutiveFailures).isEqualTo(0)
        assertThat(breaker.state).isEqualTo(CircuitBreakerState.CLOSED)
    }


    @Test
    fun `given CLOSED state when 5 consecutive failures occur then transitions to OPEN immediately`() {
        val (breaker, _) = createCircuitBreaker(failureThreshold = 5)

        repeat(4) { attempt ->
            assertThrows<IOException> {
                breaker.execute { throw IOException("Network failure #$attempt") }
            }
            assertThat(breaker.state).isEqualTo(CircuitBreakerState.CLOSED)
            assertThat(breaker.consecutiveFailures).isEqualTo(attempt + 1)
        }

        assertThrows<IOException> {
            breaker.execute { throw IOException("Network failure #5") }
        }
        assertThat(breaker.state).isEqualTo(CircuitBreakerState.OPEN)
    }

    @Test
    fun `given OPEN state when call attempted then throws CallNotPermittedException without executing`() {
        val (breaker, _) = createCircuitBreaker(failureThreshold = 5)
        repeat(5) {
            assertThrows<IOException> {
                breaker.execute { throw IOException("Error") }
            }
        }
        assertThat(breaker.state).isEqualTo(CircuitBreakerState.OPEN)
        var requestExecuted = false
        val exception = assertThrows<CallNotPermittedException> {
            breaker.execute {
                requestExecuted = true
            }
        }
        assertThat(requestExecuted).isFalse()
        assertThat(exception).isInstanceOf(CallNotPermittedException::class.java)
    }
}
