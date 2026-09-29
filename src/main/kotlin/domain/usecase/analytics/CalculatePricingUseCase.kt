package domain.usecase.analytics

import domain.model.PricingRequest
import domain.pricing.RoutePricingEngine
import domain.model.PricingRequest

class CalculatePricingUseCase(
    private val pricingEngine: RoutePricingEngine
) {
    public operator fun invoke(request: PricingRequest): Double {
        pricingEngine.setStrategy(request.strategy)
        val baseCost = pricingEngine.calculateCost(
            request.pkg.weight,
            request.route.distanceKm
        )
        return request.component.calculateTransitRate(baseCost)
    }
}
