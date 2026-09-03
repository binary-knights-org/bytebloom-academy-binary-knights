package domain.usecase

import domain.model.Vehicle
import domain.model.Package
import domain.repository.VehicleRepository

data class PackageVehicleAssignment(
    val packages: List<Package>,
    val vehicle: Vehicle
)

class AssignPackagesToAvailableVehicleUseCase(
    private val findPackagesForConsolidationUseCase: FindPackagesForConsolidationUseCase,
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(): List<PackageVehicleAssignment> {
        val packageGroups = findPackagesForConsolidationUseCase()

        return packageGroups.mapNotNull { packages ->
            val origin = packages.first().originHub
            val totalWeight = packages.sumOf { it.weight }

            val suitableVehicle = vehicleRepository.getAllVehicles()
                .filter { it.currentHub == origin }
                .firstOrNull { vehicle ->
                    vehicle.maxCapacityKg - vehicle.currentLoadKg >= totalWeight
                }

            if (suitableVehicle != null) {
                packages.forEach { pkg ->
                    suitableVehicle.loadPackage(pkg)
                }

                PackageVehicleAssignment(
                    packages = packages,
                    vehicle = suitableVehicle
                )
            } else {
                null
            }
        }
    }
}
