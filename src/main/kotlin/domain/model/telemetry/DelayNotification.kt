package domain.model.telemetry

data class DelayNotification(
    val vehicleId: String,
    val delayMinutes: Int,
    val reason: String,
    val currentSpeedKmh: Double,
    val fuelPercentage: Double,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)
