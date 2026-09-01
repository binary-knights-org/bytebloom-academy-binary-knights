package domain.usecase

import domain.model.Package
import domain.model.Vehicle
import domain.repository.VehicleRepository

class SuggestBestVehicleForPackageUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(pkg: Package): Vehicle? {
        return vehicleRepository.getAllVehicles()
            .filter { vehicle -> hasSufficientRemainingCapacity(vehicle, pkg) }
            .minByOrNull { it.costPerKm }
    }

    private fun hasSufficientRemainingCapacity(vehicle: Vehicle, pkg: Package): Boolean {
        val currentLoad = vehicle.currentHub.cargoQueue.sumOf { it.weight }
        val remainingCapacity = vehicle.maxCapacityKg - currentLoad
        return remainingCapacity >= pkg.weight
    }
}

