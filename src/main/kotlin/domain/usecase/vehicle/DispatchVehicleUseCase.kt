package domain.usecase.vehicle

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class DispatchVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(
        vehicle: Vehicle,
        warehouse: Warehouse
    ): List<Package> {
        val targetWarehouse = warehouseRepository.getAll()
            .firstOrNull { it.id == warehouse.id }

        val targetVehicle = targetWarehouse?.let { matchedWarehouse ->
            vehicleRepository.getAll().firstOrNull {
                it.id == vehicle.id &&
                        it.currentHub.id == matchedWarehouse.id
            }
        }

        return if (targetWarehouse != null && targetVehicle != null) {
            dispatchPackages(targetVehicle, targetWarehouse)
        } else {
            emptyList()
        }
    }

    private fun dispatchPackages(
        vehicle: Vehicle,
        warehouse: Warehouse
    ): List<Package> {
        val loadedPackages = mutableListOf<Package>()

        for (pkg in warehouse.cargoQueue.toList()) {
            if (vehicle.loadPackage(pkg)) {
                warehouse.removePackage(pkg)
                loadedPackages.add(pkg)
            }
        }

        return loadedPackages
    }
}
