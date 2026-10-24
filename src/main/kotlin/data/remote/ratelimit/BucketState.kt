package data.remote.ratelimit

data class BucketState(
    val tokens: Double,
    val lastRefillNanos: Long
)
