package domain.command

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.usecase.DispatchVehicleUseCase


class DispatchVehicleCommand(
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val vehicle: Vehicle,
    private val warehouse: Warehouse
) : Command {

    private val loadedPackages = mutableListOf<Package>()

    override val description: String
        get() = "DispatchVehicleCommand(vehicle=${vehicle.id}, warehouse=${warehouse.id})"

    override fun execute(): Boolean {
        loadedPackages.clear()
        val result = dispatchVehicleUseCase(vehicle, warehouse)
        if (result.isNotEmpty()) {
            loadedPackages.addAll(result)
            return true
        }
        return false
    }

    override fun undo(): Boolean {
        if (loadedPackages.isEmpty()) return false
        loadedPackages.forEach { pkg ->
            warehouse.addPackage(pkg)
        }
        vehicle.clearCargo()
        return true
    }
}
