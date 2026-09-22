package data.remote.base

import data.exception.NetworkUnavailableException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.logging.Logger
import kotlin.time.Duration.Companion.milliseconds

abstract class BaseRemoteDataSource {

    private val logger = Logger.getLogger(this::class.java.simpleName)

    protected suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): Result<T> {

        var delayMs = initialDelayMs

        for (attempt in 1..(maxRetries + 1)) {
            val result = runCatching { block() }

            if (result.isSuccess) {
                logSuccess(attempt)
                return result
            }

            val error = result.exceptionOrNull()!!

            if (shouldAbortRetry(error, attempt, maxRetries)) {
                return Result.failure(error)
            }

            logRetryAttempt(attempt, delayMs)
            delay(delayMs.milliseconds)
            delayMs = (delayMs * factor).toLong()
        }

        error("Unreachable")
    }

    private fun shouldAbortRetry(error: Throwable, attempt: Int, maxRetries: Int): Boolean {
        return when {
            !isRetryable(error) -> {
                logger.warning(
                    "[Telemetry] Attempt Number: $attempt | Non-retryable error: " +
                            "${error::class.simpleName} | Eventual Outcome: ABORTED")
                true
            }
            attempt > maxRetries -> {
                logger.severe("[Telemetry] Attempt Number: $attempt | Max retries reached | Eventual Outcome: FAILED")
                true
            }
            else -> false
        }
    }

    private fun logSuccess(attempt: Int) {
        logger.info("[Telemetry] Attempt Number: $attempt | Eventual Outcome: SUCCESS")
    }

    private fun logRetryAttempt(attempt: Int, delayMs: Long) {
        logger.info("[Telemetry] Attempt Number: $attempt failed. Calculated backoff delay: ${delayMs}ms. Retrying...")
    }

    private fun isRetryable(throwable: Throwable): Boolean {
        return throwable is NetworkUnavailableException ||
                throwable is SocketTimeoutException ||
                throwable is ConnectException ||
                throwable is IOException ||
                throwable is UnresolvedAddressException
    }
}
