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
    fun `given remote data source when 5 network failures occur then circuit breaker trips to OPEN and subsequent calls fail fast`() = runTest {
        val circuitBreaker = CircuitBreaker(failureThreshold = 5, resetTimeoutMillis = 5000L)
        val rateLimiter = RateLimiter(maxBucketCapacity = 10, refillRatePerSecond = 100.0)
        val dataSource = TestRemoteDataSource(rateLimiter, circuitBreaker)

        // 5 consecutive network errors
        repeat(5) {
            assertThrows<NetworkUnavailableException> {
                dataSource.testCall<String> {
                    throw IOException("Network connection dropped")
                }
            }
        }

        // Breaker should now be OPEN
        assertThat(circuitBreaker.state).isEqualTo(CircuitBreakerState.OPEN)

        var blockExecuted = false
        // 6th call should immediately fail fast without initiating network request
        assertThrows<CallNotPermittedException> {
            dataSource.testCall {
                blockExecuted = true
                "network data"
            }
        }

        assertThat(blockExecuted).isFalse()
    }
}
