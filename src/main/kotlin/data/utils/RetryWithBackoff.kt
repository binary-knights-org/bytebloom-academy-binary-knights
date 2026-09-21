package data.utils

import data.exception.NetworkUnavailableException
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

private fun isRetryable(throwable: Throwable): Boolean {
    return throwable is NetworkUnavailableException ||
            throwable is SocketTimeoutException ||
            throwable is ConnectException ||
            throwable is IOException
}

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): Result<T> {

    var delayMs = initialDelayMs
    var finalResult: Result<T>? = null

    repeat(maxRetries + 1) { attemptIndex ->

        val attempt = attemptIndex + 1
        val result = runCatching { block() }

        if (result.isSuccess) {
            println("[Retry] Success on attempt $attempt")
            finalResult = result
            return@repeat
        }

        val error = result.exceptionOrNull()!!

        if (!isRetryable(error)) {
            println("[Retry] Non-retryable error: ${error::class.simpleName}")
            finalResult = Result.failure(error)
            return@repeat
        }

        if (attemptIndex == maxRetries) {
            println("[Retry] Failed after $attempt attempts")
            finalResult = Result.failure(error)
            return@repeat
        }

        println(
            "[Retry] Attempt $attempt failed. " +
                    "Retrying in ${delayMs}ms"
        )

        delay(delayMs)
        delayMs = (delayMs * factor).toLong()
    }

    return finalResult
        ?: Result.failure(
            IllegalStateException("Unexpected state")
        )
}
