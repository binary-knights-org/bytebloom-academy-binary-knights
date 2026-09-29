package domain.usecase.analytics

import domain.model.PricingRequest
import domain.pricing.RoutePricingEngine

class CalculatePricingUseCase(
    private val pricingEngine: RoutePricingEngine
) {
    operator fun invoke(request: PricingRequest): Double {
        pricingEngine.setStrategy(request.strategy)
        val baseCost = pricingEngine.calculateCost(
            request.pkg.weight,
            request.route.distanceKm
        )
        return request.component.calculateTransitRate(baseCost)
    }
}
