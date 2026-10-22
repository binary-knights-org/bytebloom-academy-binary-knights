package data.remote.base

import com.google.common.truth.Truth.assertThat
import data.exception.CallNotPermittedException
import data.exception.NetworkUnavailableException
import data.remote.circuitbreaker.CircuitBreaker
import data.remote.circuitbreaker.CircuitBreakerState
import data.remote.ratelimit.RateLimiter
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class BaseRemoteDataSourceCircuitBreakerTest {

    private class TestRemoteDataSource(
        rateLimiter: RateLimiter,
        circuitBreaker: CircuitBreaker
    ) : BaseRemoteDataSource(rateLimiter, circuitBreaker) {

        suspend fun <T> testCall(block: suspend () -> T): T =
            safeRemoteCall("Unreachable", "IO Error", block)
    }

    @Test
    fun `given 5 network failures then breaker trips to OPEN and next call fails fast`() = runTest {
        val circuitBreaker = CircuitBreaker(failureThreshold = 5, resetTimeoutMillis = 5000L)
        val rateLimiter = RateLimiter(maxBucketCapacity = 10, refillRatePerSecond = 100.0)
        val dataSource = TestRemoteDataSource(rateLimiter, circuitBreaker)
        repeat(5) {
            assertThrows<NetworkUnavailableException> {
                dataSource.testCall<String> {
                    throw IOException("Network connection dropped")
                }
            }
        }
        assertThat(circuitBreaker.state).isEqualTo(CircuitBreakerState.OPEN)
        var blockExecuted = false
        assertThrows<CallNotPermittedException> {
            dataSource.testCall {
                blockExecuted = true
            }
        }
        assertThat(blockExecuted).isFalse()
    }
}
