package domain.usecase

import domain.model.PackageComponent
import domain.model.Route
import domain.pricing.DispatchStrategy
import domain.pricing.RoutePricingEngine

class CalculatePricingUseCase(
    private val pricingEngine : RoutePricingEngine
) {
    operator fun invoke(pkg: PackageComponent ,route: Route , strategy: DispatchStrategy ): Double {
    pricingEngine.setStrategy(strategy)
    val baseCost =  pricingEngine.calculateCost(pkg.packages.weight,route.distanceKm)
    return  pkg.calculateTransitRate(baseCost)
    }

}
