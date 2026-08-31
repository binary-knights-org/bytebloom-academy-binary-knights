package domain.usecase

import domain.model.Vehicle
import domain.repository.VehicleRepository

private const val ZERO_WEIGHT = 0.0

class FindUnderutilizedVehiclesUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(utilizationThreshold: Double): List<Vehicle> {
        val allVehicles = vehicleRepository.getAllVehicles()

        return allVehicles.filter { vehicle ->
            isUnderutilized(vehicle, utilizationThreshold)
        }
    }

    private fun isUnderutilized(vehicle: Vehicle, utilizationThreshold: Double): Boolean {
        if (vehicle.maxCapacityKg <= ZERO_WEIGHT) return false
        val hubQueueWeight = vehicle.currentHub.cargoQueue.sumOf { it.weight }
        return hubQueueWeight > ZERO_WEIGHT && hubQueueWeight < vehicle.maxCapacityKg * utilizationThreshold
    }
}
