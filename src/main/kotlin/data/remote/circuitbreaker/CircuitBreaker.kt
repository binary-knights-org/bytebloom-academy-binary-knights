package data.remote.circuitbreaker

import data.exception.CallNotPermittedException
import kotlinx.coroutines.CancellationException


class CircuitBreaker(
    val failureThreshold: Int = DEFAULT_FAILURE_THRESHOLD,
    val resetTimeoutMillis: Long = DEFAULT_RESET_TIMEOUT_MILLIS,
    private val timeProvider: () -> Long = System::currentTimeMillis
) {
    private val lock = Any()

    private var _state: CircuitBreakerState = CircuitBreakerState.CLOSED
    private var _consecutiveFailures: Int = 0
    private var openedAtTimestamp: Long = 0L
    private var isProbeInFlight: Boolean = false

    val state: CircuitBreakerState
        get() = synchronized(lock) {
            checkAndTransitionTimeout()
            _state
        }

    val consecutiveFailures: Int
        get() = synchronized(lock) { _consecutiveFailures }

    fun <T> execute(block: () -> T): T {
        acquirePermission()
        return try {
            val result = block()
            onSuccess()
            result
        } catch (t: Throwable) {
            onFailure(t)
            throw t
        }
    }

    suspend fun <T> executeSuspend(block: suspend () -> T): T {
        acquirePermission()
        return try {
            val result = block()
            onSuccess()
            result
        } catch (t: Throwable) {
            onFailure(t)
            throw t
        }
    }

    operator fun <T> invoke(block: () -> T): T = execute(block)

    private fun acquirePermission() {
        synchronized(lock) {
            checkAndTransitionTimeout()

            when (_state) {
                CircuitBreakerState.OPEN -> {
                    throw CallNotPermittedException()
                }

                CircuitBreakerState.HALF_OPEN -> {
                    if (isProbeInFlight) {
                        throw CallNotPermittedException()
                    }
                    isProbeInFlight = true
                }

                CircuitBreakerState.CLOSED -> {
                    // Call permitted
                }
            }
        }
    }

    private fun onSuccess() {
        synchronized(lock) {
            when (_state) {
                CircuitBreakerState.HALF_OPEN -> {
                    _state = CircuitBreakerState.CLOSED
                    _consecutiveFailures = 0
                    isProbeInFlight = false
                }

                CircuitBreakerState.CLOSED -> {
                    _consecutiveFailures = 0
                }

                CircuitBreakerState.OPEN -> {
                    // No-op
                }
            }
        }
    }

    private fun onFailure(throwable: Throwable) {
        if (throwable is CancellationException || throwable is CallNotPermittedException) {
            return
        }

        synchronized(lock) {
            when (_state) {
                CircuitBreakerState.HALF_OPEN -> {
                    tripToOpen()
                }

                CircuitBreakerState.CLOSED -> {
                    _consecutiveFailures++
                    if (_consecutiveFailures >= failureThreshold) {
                        tripToOpen()
                    }
                }

                CircuitBreakerState.OPEN -> {
                    // Already OPEN
                }
            }
        }
    }

    private fun tripToOpen() {
        _state = CircuitBreakerState.OPEN
        openedAtTimestamp = timeProvider()
        isProbeInFlight = false
    }

    private fun checkAndTransitionTimeout() {
        if (_state == CircuitBreakerState.OPEN) {
            val elapsed = timeProvider() - openedAtTimestamp
            if (elapsed >= resetTimeoutMillis) {
                _state = CircuitBreakerState.HALF_OPEN
                isProbeInFlight = false
            }
        }
    }

    companion object {
        const val DEFAULT_FAILURE_THRESHOLD = 5
        const val DEFAULT_RESET_TIMEOUT_MILLIS = 5000L
    }
}
