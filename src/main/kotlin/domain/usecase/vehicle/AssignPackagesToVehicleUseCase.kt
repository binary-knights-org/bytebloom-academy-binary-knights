package domain.usecase.vehicle


import domain.model.PackageVehicleAssignment
import domain.usecase.shipment.FindPackagesForConsolidationUseCase



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
