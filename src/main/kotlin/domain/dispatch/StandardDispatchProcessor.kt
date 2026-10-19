package domain.dispatch

import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.exception.InsufficientVehicleCapacityException
import domain.model.exception.InvalidDispatchPriorityException

class StandardDispatchProcessor : BaseDispatchProcessor() {

    override fun validateCargo(pkg: Package, vehicle: Vehicle) {
        val availableCapacity = vehicle.maxCapacityKg - vehicle.currentLoadKg

        if (pkg.weight > availableCapacity) {
            throw InsufficientVehicleCapacityException(
                "Standard Dispatch Failed: Package ${pkg.id} (${pkg.weight} kg) " +
                        "exceeds available capacity of vehicle ${vehicle.id}."
            )
        }

        if (pkg.priority != Priority.STANDARD) {
            throw InvalidDispatchPriorityException(
                "Standard Dispatch Failed: Package ${pkg.id} has priority ${pkg.priority}, but STANDARD is required."
            )
        }
    }

    override fun reserveVehicleCapacity(pkg: Package, vehicle: Vehicle) {
        if (!vehicle.loadPackage(pkg)) {
            throw InsufficientVehicleCapacityException(
                "Standard Dispatch Failed: Could not load package ${pkg.id} into vehicle ${vehicle.id}."
            )
        }
    }
}
