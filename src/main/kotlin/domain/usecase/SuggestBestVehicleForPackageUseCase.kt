package domain.usecase

import domain.model.PackageComponent
import domain.model.Vehicle
import domain.repository.VehicleRepository

class SuggestBestVehicleForPackageUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(pkg: PackageComponent): Vehicle {
        val eligibleVehicles = vehicleRepository.getAllVehicles().filter { it.maxCapacityKg >= pkg.weight }

        return eligibleVehicles.minByOrNull { it.costPerKm }
            ?: error("No vehicle with sufficient capacity found for package ${pkg.id}")
    }
}
