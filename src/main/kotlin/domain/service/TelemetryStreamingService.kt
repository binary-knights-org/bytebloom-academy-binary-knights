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
    private val random: Random = Random(42)
) {
    fun streamTelemetry(
        intervalMs: Long = DEFAULT_INTERVAL_MS,
        emissionCount: Int? = null
    ): Flow<VehicleTelemetry> = flow {
        val activeVehicles = resolveInitialVehicleStates()
        var round = 0

        while (currentCoroutineContext().isActive && (emissionCount == null || round < emissionCount)) {
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
            if (intervalMs > 0 && currentCoroutineContext().isActive && (emissionCount == null || round < emissionCount)) {
                delay(intervalMs)
            }
        }
    }

    private suspend fun resolveInitialVehicleStates(): List<SimulatedVehicleState> {
        val repoVehicles = runCatching { vehicleRepository?.getAll() }.getOrNull()
        if (!repoVehicles.isNullOrEmpty()) {
            return repoVehicles.mapIndexed { index, vehicle ->
                SimulatedVehicleState(
                    vehicleId = vehicle.id,
                    latitude = vehicle.currentHub.latitude,
                    longitude = vehicle.currentHub.longitude,
                    speedKmh = 75.0 - (index * 12.0).coerceAtLeast(0.0),
                    fuelPercentage = (95.0 - index * 10.0).coerceIn(10.0, 100.0)
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
            val latDelta = (random.nextDouble(-0.01, 0.01))
            val lonDelta = (random.nextDouble(-0.01, 0.01))
            latitude = (latitude + latDelta).coerceIn(-90.0, 90.0)
            longitude = (longitude + lonDelta).coerceIn(-180.0, 180.0)

            val speedVariation = random.nextDouble(-5.0, 5.0)
            speedKmh = (speedKmh + speedVariation).coerceIn(0.0, 120.0)

            val fuelBurn = random.nextDouble(0.1, 0.4)
            fuelPercentage = (fuelPercentage - fuelBurn).coerceIn(0.0, 100.0)
        }
    }

    companion object {
        const val DEFAULT_INTERVAL_MS = 1000L

        private val DEFAULT_FLEET_PRESETS = listOf(
            SimulatedVehicleState("TRK-101", 31.50, 34.46, 80.0, 92.0),
            SimulatedVehicleState("TRK-102", 31.90, 35.20, 22.0, 78.0),
            SimulatedVehicleState("TRK-103", 32.22, 35.26, 0.0, 65.0),
            SimulatedVehicleState("TRK-104", 31.53, 35.09, 85.0, 12.0),
            SimulatedVehicleState("TRK-105", 31.77, 35.21, 60.0, 50.0)
        )
    }
}
