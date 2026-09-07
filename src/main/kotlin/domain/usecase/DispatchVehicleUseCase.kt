package domain.usecase

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class DispatchVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(vehicle: Vehicle, warehouse: Warehouse): List<Package> {
        val targetWarehouse = warehouseRepository.getAllWarehouses()
            .firstOrNull { it.id == warehouse.id } ?: return emptyList()

        val targetVehicle = vehicleRepository.getAllVehicles()
            .firstOrNull { it.id == vehicle.id && it.currentHub.id == targetWarehouse.id } ?: return emptyList()

        val queueCopy = ArrayList(targetWarehouse.cargoQueue)
        val loaded = queueCopy.fold(
            mutableListOf<Package>()
        ) { loaded, pkg ->

            if (targetVehicle.loadPackage(pkg)) {
                if (targetVehicle != vehicle) {
                    vehicle.loadPackage(pkg)
                }
                targetWarehouse.removePackage(pkg)
                if (targetWarehouse != warehouse) {
                    warehouse.removePackage(pkg)
                }
                loaded.add(pkg)
            }

            loaded
        }
        return loaded
    }
}



