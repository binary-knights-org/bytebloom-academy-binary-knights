package domain.dispatch

import domain.model.Package
import domain.model.Vehicle

abstract class BaseDispatchProcessor {

    fun dispatch(pkg : Package , vehicle: Vehicle){
        validateCargo(pkg,vehicle)
        reserveVehicleCapacity(pkg,vehicle)
        updateShipmentState(pkg)
        notifyDispatchStatus(pkg, vehicle)
    }

    protected abstract fun validateCargo(pkg : Package, vehicle : Vehicle)
    protected abstract fun reserveVehicleCapacity(pkg : Package, vehicle : Vehicle)
    protected open fun updateShipmentState(pkg : Package): Package {
        pkg.assignToVehicle()
        return pkg
    }
    protected open fun notifyDispatchStatus(pkg: Package , vehicle: Vehicle){}
}
