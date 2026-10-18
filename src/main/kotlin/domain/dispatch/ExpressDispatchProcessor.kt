package domain.dispatch

import domain.model.DispatchNotification
import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.exception.InsufficientVehicleCapacityException
import domain.model.exception.InvalidDispatchPriorityException

class ExpressDispatchProcessor : BaseDispatchProcessor() {

    override fun validateCargo(pkg: Package, vehicle: Vehicle) {
        val availableCapacity = vehicle.maxCapacityKg - vehicle.currentLoadKg

        if (pkg.weight > availableCapacity) {
            throw InsufficientVehicleCapacityException(
                "Express Dispatch Failed: Package ${pkg.id} (${pkg.weight} kg) " +
                        "exceeds available capacity of vehicle ${vehicle.id}."
            )
        }

        if (pkg.priority != Priority.URGENT) {
            throw InvalidDispatchPriorityException(
                "Express Dispatch Failed: Package ${pkg.id} has priority ${pkg.priority}, " +
                        "but EXPRESS (URGENT) is required."
            )
        }
    }

    override fun reserveVehicleCapacity(pkg: Package, vehicle: Vehicle) {
        if (!vehicle.loadPackage(pkg)) {
            throw InsufficientVehicleCapacityException(
                "Express Dispatch Failed: Priority load failed for package ${pkg.id} into vehicle ${vehicle.id}."
            )
        }
    }

    override fun notifyDispatchStatus(pkg: Package, vehicle: Vehicle): DispatchNotification {
        return DispatchNotification(
            packageId = pkg.id,
            vehicleId = vehicle.id,
            message = "URGENT DISPATCH: Express Package ${pkg.id} successfully assigned to Vehicle ${vehicle.id}!"
        )
    }
}
