package domain.model.telemetry

import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import kotlin.math.cos
import kotlin.math.hypot

data class GpsCoordinate(
    val latitude: Double,
    val longitude: Double
) {
    init {
        if (latitude !in VehicleTelemetry.MIN_LATITUDE..VehicleTelemetry.MAX_LATITUDE) {
            throw InvalidLatitudeException()
        }
        if (longitude !in VehicleTelemetry.MIN_LONGITUDE..VehicleTelemetry.MAX_LONGITUDE) {
            throw InvalidLongitudeException()
        }
    }

    fun distanceMetersTo(other: GpsCoordinate): Double {
        val meanLatitudeRadians = Math.toRadians((latitude + other.latitude) / MEAN_DIVISOR)
        val northMeters = Math.toRadians(other.latitude - latitude) * EARTH_RADIUS_METERS
        val eastMeters = Math.toRadians(other.longitude - longitude) *
                cos(meanLatitudeRadians) * EARTH_RADIUS_METERS
        return hypot(eastMeters, northMeters)
    }

    companion object {
        const val EARTH_RADIUS_METERS = 6_371_008.8
        private const val MEAN_DIVISOR = 2.0
    }
}
