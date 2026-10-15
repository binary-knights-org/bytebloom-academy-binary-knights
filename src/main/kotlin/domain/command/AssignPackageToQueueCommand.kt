package domain.command

import domain.model.Package
import domain.model.Warehouse
import domain.usecase.shipment.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase,
    private val pkg: Package,
    private val warehouse: Warehouse
) : Command {

    private var previousQueue: List<Package> = emptyList()
    private var wasExecuted = false

    override val description: String
        get() = "AssignPackageToQueueCommand(package=${pkg.id}, warehouse=${warehouse.id})"

    override suspend  fun execute(): Boolean {
        previousQueue = warehouse.cargoQueue.toList()
        return try {
            assignPackageToCargoQueueUseCase(warehouse, pkg)
            wasExecuted = true
            true
        } catch (exception: Exception){
            warehouse.restoreCargoQueue(previousQueue)
            wasExecuted = false
            throw exception
        }
    }

    override suspend  fun undo(): Boolean {
        if (!wasExecuted) return false

        warehouse.restoreCargoQueue(previousQueue)
        wasExecuted = true
        return true
    }
}
