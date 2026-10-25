package domain.usecase.vehicle

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DispatchVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(
        vehicle: Vehicle,
        warehouse: Warehouse
    ): List<Package> {
        val (warehouses , vehicles) = coroutineScope {
            val w = async { warehouseRepository.getAll() }
            val v = async { vehicleRepository.getAll() }
            w.await() to v.await()
        }

        val targetWarehouse =  warehouses.firstOrNull { it.id == warehouse.id }

        val targetVehicle = targetWarehouse?.let { matchedWarehouse ->
            vehicles.firstOrNull {
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
