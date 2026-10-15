package domain.model.telemetry

data class FleetTrackingCriteria(
    val lowSpeedThresholdKmh: Double = DEFAULT_LOW_SPEED_THRESHOLD_KMH,
    val lowFuelThresholdPercent: Double = DEFAULT_LOW_FUEL_THRESHOLD_PERCENT,
    val targetVehicleIds: Set<String>? = null,
    val minDelayMinutes: Int = DEFAULT_MIN_DELAY_MINUTES
) {
    companion object {
        const val DEFAULT_LOW_SPEED_THRESHOLD_KMH = 30.0
        const val DEFAULT_LOW_FUEL_THRESHOLD_PERCENT = 15.0
        const val DEFAULT_MIN_DELAY_MINUTES = 1
    }
}
