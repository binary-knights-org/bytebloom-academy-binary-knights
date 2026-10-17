package domain.service

import domain.model.telemetry.VehicleTelemetry
import domain.repository.VehicleRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.random.Random

class TelemetryStreamingService(
    private val vehicleRepository: VehicleRepository? = null,
    private val random: Random = Random(DEFAULT_RANDOM_SEED)
) {
    fun streamTelemetry(
        intervalMs: Long = DEFAULT_INTERVAL_MS,
        emissionCount: Int? = null
    ): Flow<VehicleTelemetry> = flow {
        val activeVehicles = resolveInitialVehicleStates()
        var round = 0

        while (currentCoroutineContext().isActive && shouldContinueStreaming(round, emissionCount)) {
            for (state in activeVehicles) {
                if (!currentCoroutineContext().isActive) break

                state.advance(random)
                val update = VehicleTelemetry(
                    vehicleId = state.vehicleId,
                    latitude = state.latitude,
                    longitude = state.longitude,
                    speedKmh = state.speedKmh,
                    fuelPercentage = state.fuelPercentage,
                    timestamp = System.currentTimeMillis()
                )
                emit(update)
            }

            round++
            val hasMoreEmissions = shouldContinueStreaming(round, emissionCount)
            if (intervalMs > 0 && currentCoroutineContext().isActive && hasMoreEmissions) {
                delay(intervalMs)
            }
        }
    }

    private fun shouldContinueStreaming(round: Int, emissionCount: Int?): Boolean {
        return emissionCount == null || round < emissionCount
    }

    private suspend fun resolveInitialVehicleStates(): List<SimulatedVehicleState> {
        val repoVehicles = runCatching { vehicleRepository?.getAll() }.getOrNull()
        if (!repoVehicles.isNullOrEmpty()) {
            return repoVehicles.mapIndexed { index, vehicle ->
                SimulatedVehicleState(
                    vehicleId = vehicle.id,
                    latitude = vehicle.currentHub.latitude,
                    longitude = vehicle.currentHub.longitude,
                    speedKmh = INITIAL_REPO_SPEED_KMH - (index * INITIAL_REPO_SPEED_STEP).coerceAtLeast(0.0),
                    fuelPercentage = (INITIAL_REPO_FUEL_PERCENT - index * INITIAL_REPO_FUEL_STEP)
                        .coerceIn(INITIAL_MIN_FUEL_PERCENT, MAX_FUEL_PERCENT)
                )
            }
        }

        return DEFAULT_FLEET_PRESETS.map { it.copy() }
    }

    internal data class SimulatedVehicleState(
        val vehicleId: String,
        var latitude: Double,
        var longitude: Double,
        var speedKmh: Double,
        var fuelPercentage: Double
    ) {
        fun advance(random: Random) {
            val latDelta = random.nextDouble(MIN_COORDINATE_DELTA, MAX_COORDINATE_DELTA)
            val lonDelta = random.nextDouble(MIN_COORDINATE_DELTA, MAX_COORDINATE_DELTA)
            latitude = (latitude + latDelta).coerceIn(MIN_LATITUDE, MAX_LATITUDE)
            longitude = (longitude + lonDelta).coerceIn(MIN_LONGITUDE, MAX_LONGITUDE)

            val speedVariation = random.nextDouble(MIN_SPEED_VARIATION, MAX_SPEED_VARIATION)
            speedKmh = (speedKmh + speedVariation).coerceIn(MIN_SPEED_KMH, MAX_SPEED_KMH)

            val fuelBurn = random.nextDouble(MIN_FUEL_BURN, MAX_FUEL_BURN)
            fuelPercentage = (fuelPercentage - fuelBurn).coerceIn(MIN_FUEL_PERCENT, MAX_FUEL_PERCENT)
        }
    }

    companion object {
        const val DEFAULT_INTERVAL_MS = 1000L
        private const val DEFAULT_RANDOM_SEED = 42

        private const val INITIAL_REPO_SPEED_KMH = 75.0
        private const val INITIAL_REPO_SPEED_STEP = 12.0
        private const val INITIAL_REPO_FUEL_PERCENT = 95.0
        private const val INITIAL_REPO_FUEL_STEP = 10.0
        private const val INITIAL_MIN_FUEL_PERCENT = 10.0

        private const val MIN_COORDINATE_DELTA = -0.01
        private const val MAX_COORDINATE_DELTA = 0.01
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0

        private const val MIN_SPEED_VARIATION = -5.0
        private const val MAX_SPEED_VARIATION = 5.0
        private const val MIN_SPEED_KMH = 0.0
        private const val MAX_SPEED_KMH = 120.0

        private const val MIN_FUEL_BURN = 0.1
        private const val MAX_FUEL_BURN = 0.4
        private const val MIN_FUEL_PERCENT = 0.0
        private const val MAX_FUEL_PERCENT = 100.0

        private val DEFAULT_FLEET_PRESETS = listOf(
            SimulatedVehicleState("TRK-101", 31.50, 34.46, 80.0, 92.0),
            SimulatedVehicleState("TRK-102", 31.90, 35.20, 22.0, 78.0),
            SimulatedVehicleState("TRK-103", 32.22, 35.26, 0.0, 65.0),
            SimulatedVehicleState("TRK-104", 31.53, 35.09, 85.0, 12.0),
            SimulatedVehicleState("TRK-105", 31.77, 35.21, 60.0, 50.0)
        )
    }
}
