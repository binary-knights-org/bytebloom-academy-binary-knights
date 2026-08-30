package domain.usecase

import domain.decorator.ColdChainDecorator
import domain.decorator.FragileHandlingDecorator
import domain.decorator.PackageDecorator
import domain.model.Package
import domain.model.PackageComponent
import domain.repository.WarehouseRepository


class FindFragileShipmentsNeedingColdChainUseCase(
    private val warehouseRepository: WarehouseRepository,
) {

    operator fun invoke(): List<Package> {
        val warehouses = warehouseRepository.getAllWarehouses()
        return warehouses
            .flatMap { it.cargoQueue }
            .filter { it.isFragile() && it.needsColdChain() }
    }

    private fun PackageComponent.isFragile(): Boolean {
        val layers = generateSequence(this) { (it as? PackageDecorator)?.wrappedPackage }
        return layers.any { it is FragileHandlingDecorator }
    }

    private fun PackageComponent.needsColdChain(): Boolean {
        return generateSequence(this) { (it as? PackageDecorator)?.wrappedPackage }
            .any { it is ColdChainDecorator }
    }
}
