package data.remote.circuitbreaker

/**
 * Three-state topology representing the lifecycle of the Circuit Breaker:
 * - [CLOSED]: Normal operations; requests pass through and remote call failures are monitored.
 * - [OPEN]: Failing fast; requests are immediately rejected without initiating any HTTP/database calls.
 * - [HALF_OPEN]: Probing recovery; allows a single test probe request through to check service health.
 */
enum class CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}
