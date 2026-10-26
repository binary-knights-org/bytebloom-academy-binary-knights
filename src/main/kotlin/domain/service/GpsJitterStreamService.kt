package domain.service

import domain.model.telemetry.GpsCoordinate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


class GpsJitterStreamService(
    private val seed: Int = DEFAULT_RANDOM_SEED
) {
    fun streamNoisyCoordinates(
        start: GpsCoordinate = DEFAULT_START,
        maxJitterMeters: Double = DEFAULT_MAX_JITTER_METERS,
        intervalMs: Long = DEFAULT_INTERVAL_MS,
        emissionCount: Int? = null
    ): Flow<GpsCoordinate> = flow {
        val random = Random(seed)
        var step = 0

        while (shouldContinue(step, emissionCount)) {
            emit(addJitter(truePositionAt(start, step), maxJitterMeters, random))
            step++

            if (intervalMs > 0 && shouldContinue(step, emissionCount)) {
                delay(intervalMs.milliseconds)
            }
        }
    }

    fun truePositionAt(start: GpsCoordinate, step: Int): GpsCoordinate =
        GpsCoordinate(
            latitude = start.latitude + step * LATITUDE_STEP_DEGREES,
            longitude = start.longitude + step * LONGITUDE_STEP_DEGREES
        )

    private fun shouldContinue(step: Int, emissionCount: Int?): Boolean =
        emissionCount == null || step < emissionCount

    private fun addJitter(
        position: GpsCoordinate,
        maxJitterMeters: Double,
        random: Random
    ): GpsCoordinate {
        val northMeters = randomOffsetMeters(maxJitterMeters, random)
        val eastMeters = randomOffsetMeters(maxJitterMeters, random)

        val latitudeOffset = Math.toDegrees(northMeters / GpsCoordinate.EARTH_RADIUS_METERS)
        val longitudeOffset = Math.toDegrees(
            eastMeters / (GpsCoordinate.EARTH_RADIUS_METERS * cos(Math.toRadians(position.latitude)))
        )

        return GpsCoordinate(
            latitude = position.latitude + latitudeOffset,
            longitude = position.longitude + longitudeOffset
        )
    }

    private fun randomOffsetMeters(maxJitterMeters: Double, random: Random): Double =
        if (maxJitterMeters <= NO_JITTER) NO_JITTER
        else random.nextDouble(-maxJitterMeters, maxJitterMeters)

    companion object {
        const val DEFAULT_INTERVAL_MS = 1000L
        const val DEFAULT_MAX_JITTER_METERS = 20.0
        val DEFAULT_START = GpsCoordinate(latitude = 32.2211, longitude = 35.2544)

        private const val DEFAULT_RANDOM_SEED = 42
        private const val NO_JITTER = 0.0

        private const val LATITUDE_STEP_DEGREES = 0.0001
        private const val LONGITUDE_STEP_DEGREES = 0.00015
    }
}
