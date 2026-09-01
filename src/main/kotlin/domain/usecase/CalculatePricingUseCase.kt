package domain.usecase

import domain.model.PackageComponent
import domain.model.Route
import domain.model.Package
import domain.pricing.DispatchStrategy
import domain.pricing.RoutePricingEngine

class CalculatePricingUseCase(
    private val pricingEngine: RoutePricingEngine
) {
    operator fun invoke(request: PricingRequest): Double {
        pricingEngine.setStrategy(request.strategy)
        val baseCost = pricingEngine.calculateCost(request.pkg.weight, request.route.distanceKm)
        return request.component.calculateTransitRate(baseCost)
    }
}

data class PricingRequest(
    val pkg: Package,
    val component: PackageComponent,
    val route: Route,
    val strategy: DispatchStrategy
)
