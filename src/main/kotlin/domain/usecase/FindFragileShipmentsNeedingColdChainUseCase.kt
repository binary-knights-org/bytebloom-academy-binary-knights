package domain.usecase

import domain.decorator.ColdChainDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.Package
import domain.model.Route
import domain.model.Warehouse
import domain.pricing.FragileStrategy
import domain.repository.WarehouseRepository

private const val HIGH_RISK_COST_THRESHOLD = 200.0

class FindFragileShipmentsNeedingColdChainUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val calculatePricingUseCase: CalculatePricingUseCase
) {

    operator fun invoke(): List<Package> {
        val warehouses = warehouseRepository.getAllWarehouses()

        return warehouses
            .flatMap { it.cargoQueue }
            .filter { pkg -> exceedsCostThreshold(pkg, warehouses) }
    }

    private fun exceedsCostThreshold(pkg: Package, warehouses: List<Warehouse>): Boolean {
        val route = findRoute(pkg, warehouses) ?: return false

        val decoratedPackage = ColdChainDecorator(FragileHandlingDecorator(pkg))
        val finalCost = calculatePricingUseCase(decoratedPackage, route, FragileStrategy())

        return finalCost > HIGH_RISK_COST_THRESHOLD
    }

    private fun findRoute(pkg: Package, warehouses: List<Warehouse>): Route? {
        return warehouses
            .flatMap { it.outgoingRoutes }
            .find { it.originHub.id == pkg.originHub.id && it.destinationHub.id == pkg.destinationHub.id }
    }
}

