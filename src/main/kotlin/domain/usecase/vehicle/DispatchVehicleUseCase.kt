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
    suspend operator fun invoke(vehicle: Vehicle, warehouse: Warehouse): List<Package> {
        val targetWarehouse = warehouseRepository.getAll()
            .firstOrNull { it.id == warehouse.id }
            ?: return emptyList()

        val targetVehicle = vehicleRepository.getAll()
            .firstOrNull {
                it.id == vehicle.id &&
                        it.currentHub.id == targetWarehouse.id
            }
            ?: return emptyList()

        val loadedPackages = mutableListOf<Package>()

        for (pkg in targetWarehouse.cargoQueue.toList()) {
            if (targetVehicle.loadPackage(pkg)) {
                targetWarehouse.removePackage(pkg)
                loadedPackages.add(pkg)
            }
        }
        return loadedPackages
    }
}
