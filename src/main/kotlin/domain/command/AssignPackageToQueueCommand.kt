package domain.command

import domain.model.Package
import domain.model.Warehouse
import domain.usecase.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase,
    private val pkg: Package,
    private val warehouse: Warehouse
) : Command {

    override val description: String
        get() = "AssignPackageToQueueCommand(package=${pkg.id}, warehouse=${warehouse.id})"

    override fun execute(): Boolean {
        assignPackageToCargoQueueUseCase(warehouse, pkg)
        return true
    }

    override fun undo(): Boolean {
        return warehouse.removePackage(pkg)
    }
}
