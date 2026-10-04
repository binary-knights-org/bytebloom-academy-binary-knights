package domain.dispatch

import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.exception.InsufficientVehicleCapacityException
import domain.model.exception.InvalidDispatchPriorityException


class ExpressDispatchProcessor : BaseDispatchProcessor() {
    override fun validateCargo(pkg: Package, vehicle: Vehicle) {
        val availableCapacity = vehicle.maxCapacityKg - vehicle.loadedCargo.sumOf { it.weight }

        pkg.takeIf { it.weight > availableCapacity }?.let {
            throw InsufficientVehicleCapacityException(
                "Express Dispatch Failed: Package ${it.id} (${it.weight} kg) )."
            )
        }

        pkg.takeUnless { it.priority == Priority.URGENT }?.let {
            throw InvalidDispatchPriorityException(
                "Express Dispatch Failed: Package ${it.id} has priority ${it.priority}, but EXPRESS is required."
            )
        }
    }

    override fun reserveVehicleCapacity(pkg: Package, vehicle : Vehicle) {
    vehicle.takeUnless { it.loadPackage(pkg) }?.let {
        throw InsufficientVehicleCapacityException(
            "Express Dispatch Failed: Priority load failed for package ${pkg.id} into vehicle ${vehicle.id}."
            )
        }
    }


    override fun notifyDispatchStatus(pkg: Package , vehicle: Vehicle) {
        println("URGENT DISPATCH: Express Package ${pkg.id} successfully assigned to Vehicle ${vehicle.id}!")
    }
}

