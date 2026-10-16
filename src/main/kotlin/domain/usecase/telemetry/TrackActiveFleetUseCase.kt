package domain.usecase.telemetry

import domain.model.telemetry.DelayNotification
import domain.model.telemetry.FleetTrackingCriteria
import domain.model.telemetry.VehicleTelemetry
import domain.service.TelemetryStreamingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform

class TrackActiveFleetUseCase(
    private val telemetryService: TelemetryStreamingService
) {

    operator fun invoke(
        criteria: FleetTrackingCriteria = FleetTrackingCriteria(),
        intervalMs: Long = TelemetryStreamingService.DEFAULT_INTERVAL_MS,
        emissionCount: Int? = null
    ): Flow<DelayNotification> {
        return telemetryService.streamTelemetry(
            intervalMs = intervalMs,
            emissionCount = emissionCount
        )
            .filter { update ->
                val vehicleMatches = criteria.targetVehicleIds == null ||
                        update.vehicleId in criteria.targetVehicleIds

                val hasPotentialDelay = update.speedKmh <= criteria.lowSpeedThresholdKmh ||
                        update.fuelPercentage <= criteria.lowFuelThresholdPercent

                vehicleMatches && hasPotentialDelay
            }
            .transform { update ->
                val notification = evaluateDelay(update, criteria)
                if (notification != null && notification.delayMinutes >= criteria.minDelayMinutes) {
                    emit(notification)
                }
            }
    }

    private fun evaluateDelay(
        update: VehicleTelemetry,
        criteria: FleetTrackingCriteria
    ): DelayNotification? {
        val (delayMinutes, reason) = when {
            update.speedKmh == 0.0 -> {
                STATIONARY_DELAY_MIN to "Vehicle stationary in transit. Possible breakdown or blockage."
            }
            update.speedKmh <= criteria.lowSpeedThresholdKmh -> {
                val deficitRatio = ((NOMINAL_CRUISE_SPEED_KMH - update.speedKmh) / NOMINAL_CRUISE_SPEED_KMH)
                    .coerceIn(MIN_SPEED_DEFICIT_RATIO, MAX_SPEED_DEFICIT_RATIO)
                val calculatedDelay = (deficitRatio * MAX_TRAFFIC_DELAY_MIN).toInt().coerceAtLeast(MIN_SPEED_DELAY_MIN)
                calculatedDelay to "Low transit speed (${update.speedKmh.toInt()} km/h). Traffic congestion detected."
            }
            update.fuelPercentage <= criteria.lowFuelThresholdPercent -> {
                LOW_FUEL_DETOUR_DELAY_MIN to
                    "Low fuel level (${update.fuelPercentage.toInt()}%). Detour for refueling required."
            }
            else -> return null
        }

        return DelayNotification(
            vehicleId = update.vehicleId,
            delayMinutes = delayMinutes,
            reason = reason,
            currentSpeedKmh = update.speedKmh,
            fuelPercentage = update.fuelPercentage,
            latitude = update.latitude,
            longitude = update.longitude,
            timestamp = update.timestamp
        )
    }

    companion object {
        private const val NOMINAL_CRUISE_SPEED_KMH = 80.0
        private const val MAX_TRAFFIC_DELAY_MIN = 30
        private const val MIN_SPEED_DELAY_MIN = 5
        private const val STATIONARY_DELAY_MIN = 35
        private const val LOW_FUEL_DETOUR_DELAY_MIN = 20
        private const val MIN_SPEED_DEFICIT_RATIO = 0.1
        private const val MAX_SPEED_DEFICIT_RATIO = 1.0
    }
}
