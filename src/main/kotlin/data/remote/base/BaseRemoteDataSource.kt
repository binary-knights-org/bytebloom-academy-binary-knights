package data.remote.base

import data.exception.NetworkUnavailableException
import data.remote.ratelimit.RateLimiter
import io.github.jan.supabase.exceptions.RestException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.time.Duration.Companion.milliseconds

abstract class BaseRemoteDataSource (
    private val rateLimiter: RateLimiter
){
    protected suspend fun <T> safeRemoteCall(
        unreachableMsg: String,
        ioMsg: String,
        block: suspend () -> T
    ): T = retryWithBackoff { block() }.getOrElse { error ->
        when (error) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(error.message ?: unreachableMsg)
            is IOException -> throw NetworkUnavailableException(error.message ?: ioMsg)
            else -> throw error
        }
    }

    protected suspend fun safeWriteCall(
        unreachableMsg: String,
        ioMsg: String,
        block: suspend () -> Unit
    ): Boolean = safeRemoteCall(unreachableMsg, ioMsg) {
        try {
            block()
            true
        } catch (_: RestException) {
            false
        }
    }

    protected suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): Result<T> {
        var delayMs = initialDelayMs
        for (attempt in 1..(maxRetries + 1)) {
            rateLimiter.acquire()
            val result = runCatching { block() }
            if (result.isSuccess) return result

            val error = result.exceptionOrNull()!!
            if (shouldAbortRetry(error, attempt, maxRetries)) {
                return Result.failure(error)
            }

            delay(delayMs.milliseconds)
            delayMs = (delayMs * factor).toLong()
        }
        error("Unreachable")
    }

    private fun shouldAbortRetry(error: Throwable, attempt: Int, maxRetries: Int): Boolean = when {
        !isRetryable(error) -> true
        attempt > maxRetries -> true
        else -> false
    }

    private fun isRetryable(throwable: Throwable): Boolean =
        throwable is NetworkUnavailableException ||
                throwable is SocketTimeoutException ||
                throwable is ConnectException ||
                throwable is IOException ||
                throwable is UnresolvedAddressException
}
