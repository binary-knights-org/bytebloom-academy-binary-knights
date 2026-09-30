package domain.usecase.shipment

import domain.model.Package
import domain.model.Warehouse
import domain.model.exception.ResourceNotFoundException
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class ReroutePackageUseCase(
    private val packageRepository: PackageRepository,
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(packageId: String, newDestinationId: String) {
        val packageToReroute = findPackage(packageId)
        val newDestination = findWarehouse(newDestinationId)
        val oldDestination = packageToReroute.destinationHub

        val reroutedPackage = packageToReroute.copy(destinationHub = newDestination)

        oldDestination.removePackage(packageToReroute)
        newDestination.addPackage(reroutedPackage)
    }

    private suspend  fun findPackage(packageId: String): Package {
        val packagesById = packageRepository.getAll().associateBy { it.id }
        return packagesById[packageId]
            ?: throw ResourceNotFoundException()
    }

    private suspend  fun findWarehouse(warehouseId: String): Warehouse {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return warehousesById[warehouseId]
            ?: throw ResourceNotFoundException()
    }
}
