package domain.dispatch
import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.exception.InsufficientVehicleCapacityException
import domain.model.exception.InvalidDispatchPriorityException

class StandardDispatchProcessor : BaseDispatchProcessor() {

    override fun validateCargo(pkg : Package , vehicle : Vehicle) {
        val availableCapacity = vehicle.maxCapacityKg - vehicle.currentLoadKg

        pkg.takeIf { it.weight > availableCapacity }?.let {
            throw InsufficientVehicleCapacityException(
                "Standard Dispatch Failed: Package ${it.id} (${it.weight} kg) exceeds vehicle ${vehicle.id} )."
            )
        }
        pkg.takeUnless { it.priority == Priority.STANDARD }?.let {
            throw InvalidDispatchPriorityException(
                "Standard Dispatch Failed: Package ${it.id} has priority ${it.priority}, but STANDARD is required."
            )
        }
    }

    override fun reserveVehicleCapacity(pkg: Package, vehicle : Vehicle) {
    vehicle.takeUnless { it.loadPackage(pkg)  }?.let {
        throw InsufficientVehicleCapacityException(
           "Standard Dispatch Failed: Could not load package ${pkg.id} into vehicle ${vehicle.id}."
        )
    }
    }

    override fun updateShipmentState(pkg : Package) {
        pkg.assignToVehicle()
    }

}
