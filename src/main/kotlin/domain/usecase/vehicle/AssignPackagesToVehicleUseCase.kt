package domain.usecase.vehicle

import domain.model.assignment.PackageVehicleAssignment
import domain.usecase.shipment.FindPackagesForConsolidationUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class AssignPackagesToVehicleUseCase(
    private val findPackagesForConsolidationUseCase: FindPackagesForConsolidationUseCase,
    private val findSuitableVehicleUseCase: FindSuitableVehicleUseCase
) {
    suspend operator fun invoke(): List<PackageVehicleAssignment> = coroutineScope {
        findPackagesForConsolidationUseCase()
            .map { packages ->
                async {
                    findSuitableVehicleUseCase(packages)?.let { vehicle ->
                        PackageVehicleAssignment(packages = packages, vehicle = vehicle)
                    }
                }
            }
            .awaitAll()
            .filterNotNull()
    }
}
