package domain.algorithm.optimization

import domain.model.Warehouse
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

internal class GeographicDistanceCalculator {

    fun distanceBetween(
        origin: Warehouse,
        destination: Warehouse
    ): Double {
        val latitudeDifference = calculateLatitudeDifference(origin, destination)
        val longitudeDifference = calculateLongitudeDifference(origin, destination)

        val haversine = calculateHaversine(
            origin.latitude,
            destination.latitude,
            latitudeDifference,
            longitudeDifference
        )

        return calculateGreatCircleDistance(haversine)
    }

    private fun calculateLatitudeDifference(
        origin: Warehouse,
        destination: Warehouse
    ): Double =
        Math.toRadians(destination.latitude - origin.latitude)

    private fun calculateLongitudeDifference(
        origin: Warehouse,
        destination: Warehouse
    ): Double =
        Math.toRadians(destination.longitude - origin.longitude)

    private fun calculateHaversine(
        originLatitudeDegrees: Double,
        destinationLatitudeDegrees: Double,
        latitudeDifference: Double,
        longitudeDifference: Double
    ): Double {
        val originLatitudeRadians = Math.toRadians(originLatitudeDegrees)
        val destinationLatitudeRadians = Math.toRadians(destinationLatitudeDegrees)

        val latitudeTerm = calculateHalfAngleSineSquared(latitudeDifference)
        val longitudeTerm = calculateHalfAngleSineSquared(longitudeDifference)

        return latitudeTerm +
                cos(originLatitudeRadians) *
                cos(destinationLatitudeRadians) *
                longitudeTerm
    }

    private fun calculateHalfAngleSineSquared(angleRadians: Double): Double =
        sin(angleRadians / HALF_ANGLE_DIVISOR).pow(SQUARED_POWER)

    private fun calculateGreatCircleDistance(haversine: Double): Double {
        val boundedHaversine = haversine.coerceIn(MIN_HAVERSINE, MAX_HAVERSINE)
        val centralAngle = calculateCentralAngle(boundedHaversine)

        return EARTH_RADIUS_KM * centralAngle
    }

    private fun calculateCentralAngle(haversine: Double): Double =
        CENTRAL_ANGLE_MULTIPLIER * atan2(
            sqrt(haversine),
            sqrt(MAX_HAVERSINE - haversine)
        )

    companion object {
        private const val EARTH_RADIUS_KM = 6371.0088
        private const val HALF_ANGLE_DIVISOR = 2.0
        private const val SQUARED_POWER = 2
        private const val CENTRAL_ANGLE_MULTIPLIER = 2.0
        private const val MIN_HAVERSINE = 0.0
        private const val MAX_HAVERSINE = 1.0
    }
}
