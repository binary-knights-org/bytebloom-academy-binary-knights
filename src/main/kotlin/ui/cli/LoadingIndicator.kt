package ui.cli

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LOADING_DELAY_MS = 300L
private const val MINIMUM_LOADING_TIME_MS = 1500L
private const val LOADING_DOTS_COUNT = 3
private const val CLEAR_LINE_LENGTH = 60

internal suspend fun <T> withLoadingIndicator(
    message: String,
    operation: suspend (stopLoading: suspend () -> Unit) -> T
): T = coroutineScope {
    val loadingJob = launch {
        var dotsCount = 1

        while (true) {
            print("\r${" ".repeat(CLEAR_LINE_LENGTH)}\r")
            print("\u001B[95m"+"$message${".".repeat(dotsCount)}")
            dotsCount = (dotsCount % LOADING_DOTS_COUNT) + 1
            delay(LOADING_DELAY_MS)
        }
    }

    var stopped = false

    suspend fun stopLoading() {
        if (!stopped) {
            stopped = true
            loadingJob.cancelAndJoin()
            print("\r${" ".repeat(CLEAR_LINE_LENGTH)}\r")
        }
    }

    try {
        val startTime = System.currentTimeMillis()
        val result = operation(::stopLoading)
        val elapsedTime = System.currentTimeMillis() - startTime

        if (!stopped && elapsedTime < MINIMUM_LOADING_TIME_MS) {
            delay(MINIMUM_LOADING_TIME_MS - elapsedTime)
        }

        result
    } finally {
        stopLoading()
    }
}


