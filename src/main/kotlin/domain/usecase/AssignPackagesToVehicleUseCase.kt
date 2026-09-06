package domain.usecase

import domain.model.Package
import domain.model.Vehicle

data class PackageVehicleAssignment(
    val packages: List<Package>,
    val vehicle: Vehicle
)

class AssignPackagesToVehicleUseCase {

    operator fun invoke(packages: List<Package>, vehicle: Vehicle): PackageVehicleAssignment {
        packages.forEach { pkg ->
            vehicle.loadPackage(pkg)
        }

        return PackageVehicleAssignment(
            packages = packages,
            vehicle = vehicle
        )
    }
}