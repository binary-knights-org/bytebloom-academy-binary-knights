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

    private var previousVehicleCargo: List<Package> = emptyList()
    private var previousWarehouseQueue: List<Package> = emptyList()

    override val description: String
        get() = "DispatchVehicleCommand(vehicle=${vehicle.id}, warehouse=${warehouse.id})"

    override fun execute(): Boolean {

        previousVehicleCargo = vehicle.loadedCargo.toList()
        previousWarehouseQueue = warehouse.cargoQueue.toList()

        val result = dispatchVehicleUseCase(vehicle, warehouse)

        return result.isNotEmpty()
    }

    override fun undo(): Boolean {
        warehouse.restoreCargoQueue(previousWarehouseQueue)
        vehicle.restoreCargo(previousVehicleCargo)

        return true
    }
}