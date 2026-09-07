package domain.usecase.shipment

import domain.model.Warehouse
import domain.model.Package

class AssignPackageToCargoQueueUseCase {
    operator fun invoke(
        warehouse: Warehouse,
        packageToAssign: Package
    ){
        warehouse.addPackage(packageToAssign)
        warehouse.sortCargoQueueByWeightDescending()
    }
}
