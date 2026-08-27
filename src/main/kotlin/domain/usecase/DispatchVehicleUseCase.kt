package domain.usecase

import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.Package

class DispatchVehicleUseCase {
    operator fun invoke(vehicle: Vehicle, warehouse: Warehouse): List<Package> {

        val queueCopy = ArrayList(warehouse.cargoQueue)
        val loaded = queueCopy.fold(
            mutableListOf<Package>()
        ) { loaded, pkg ->

            if (vehicle.loadPackage(pkg)) {
                warehouse.removePackage(pkg)
                loaded.add(pkg)
            }

            loaded
        }
        return loaded
    }
}
