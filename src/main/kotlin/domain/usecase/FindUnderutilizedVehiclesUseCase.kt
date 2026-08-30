package domain.usecase

import domain.model.Vehicle
import domain.repository.VehicleRepository

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
        val hubQueueWeight = vehicle.currentHub.cargoQueue.sumOf { it.weight }
        return hubQueueWeight < vehicle.maxCapacityKg * utilizationThreshold
    }
}

