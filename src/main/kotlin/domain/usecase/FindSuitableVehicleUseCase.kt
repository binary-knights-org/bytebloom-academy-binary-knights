package domain.usecase

import domain.model.Package
import domain.model.Vehicle
import domain.repository.VehicleRepository

class FindSuitableVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(packages: List<Package>): Vehicle? {
        val origin = packages.first().originHub
        val totalWeight = packages.sumOf { it.weight }

        return vehicleRepository.getAllVehicles()
            .filter { it.currentHub == origin }
            .firstOrNull { vehicle ->
                vehicle.currentLoadKg + totalWeight <= vehicle.maxCapacityKg
            }
    }
}
