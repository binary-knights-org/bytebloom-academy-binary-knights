package domain.usecase.vehicle

import domain.model.Package
import domain.model.Vehicle
import domain.usecase.shipment.FindPackagesForConsolidationUseCase

data class PackageVehicleAssignment(
    val packages: List<Package>,
    val vehicle: Vehicle
)

class AssignPackagesToVehicleUseCase(
    private val findPackagesForConsolidationUseCase: FindPackagesForConsolidationUseCase,
    private val findSuitableVehicleUseCase: FindSuitableVehicleUseCase
) {

    suspend operator fun invoke(): List<PackageVehicleAssignment> {
        val consolidationGroups = findPackagesForConsolidationUseCase()

        return consolidationGroups.mapNotNull { packages ->
            findSuitableVehicleUseCase(packages)?.let { vehicle ->
                PackageVehicleAssignment(
                    packages = packages,
                    vehicle = vehicle
                )
            }
        }
    }
}
