package domain.command

import domain.model.Package
import domain.model.Warehouse
import domain.usecase.shipment.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase,
    private val pkg: Package,
    private val warehouse: Warehouse
) : Command {

    override val description: String
        get() = "AssignPackageToQueueCommand(package=${pkg.id}, warehouse=${warehouse.id})"

    override suspend  fun execute(): Boolean {
        assignPackageToCargoQueueUseCase(warehouse, pkg)
        return true
    }

    override suspend  fun undo(): Boolean {
        return warehouse.removePackage(pkg)
    }
}
