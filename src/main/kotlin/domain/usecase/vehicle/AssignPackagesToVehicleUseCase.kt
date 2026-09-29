package domain.usecase.vehicle

import domain.model.Package
import domain.model.PackageVehicleAssignment
import domain.model.Vehicle
import domain.usecase.shipment.FindPackagesForConsolidationUseCase

class AssignPackagesToVehicleUseCase(
    private val findPackagesForConsolidationUseCase: FindPackagesForConsolidationUseCase,
    private val findSuitableVehicleUseCase: FindSuitableVehicleUseCase
) {

    public suspend operator fun invoke(): List<PackageVehicleAssignment> {
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
