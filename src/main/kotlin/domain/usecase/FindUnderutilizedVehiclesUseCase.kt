package domain.usecase

import domain.model.Vehicle
import domain.repository.VehicleRepository

class FindUnderutilizedVehiclesUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(utilizationThreshold: Double): List<Vehicle> {
        val allVehicles = vehicleRepository.getAllVehicles()
        val averageCapacity = allVehicles.map { it.maxCapacityKg }.average()

        return allVehicles.filter { vehicle ->
            isUnderutilized(vehicle, averageCapacity, utilizationThreshold)
        }
    }

    private fun isUnderutilized(
        vehicle: Vehicle,
        averageCapacity: Double,
        utilizationThreshold: Double
    ): Boolean {
        val hubQueueWeight = vehicle.currentHub.cargoQueue.sumOf { it.weight }

        return vehicle.maxCapacityKg > averageCapacity &&
                hubQueueWeight < vehicle.maxCapacityKg * utilizationThreshold
    }
}
