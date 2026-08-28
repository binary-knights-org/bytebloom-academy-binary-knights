package domain.usecase

import domain.model.Package
import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class ReroutePackageUseCase(
    private val packageRepository: PackageRepository,
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(packageId: String, newDestinationId: String) {
        val packageToReroute = findPackage(packageId)
        val newDestination = findWarehouse(newDestinationId)
        val oldDestination = packageToReroute.destinationHub

        oldDestination.removePackage(packageToReroute)
        packageToReroute.destinationHub = newDestination
        newDestination.addPackage(packageToReroute)
    }

    private fun findPackage(packageId: String): Package {
        val packagesById = packageRepository.getAllPackages().associateBy { it.id }
        return requireNotNull(packagesById[packageId]) { "Package not found: $packageId" }
    }

    private fun findWarehouse(warehouseId: String): Warehouse {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }
        return requireNotNull(warehousesById[warehouseId]) { "Warehouse not found: $warehouseId" }
    }
}
