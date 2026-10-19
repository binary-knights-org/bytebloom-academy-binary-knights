package domain.pricing

class RoutePricingEngine(var strategy: DispatchStrategy) {

    fun calculateCost(weight: Double, distance: Double): Double {
        val baseCost = strategy.calculateTransitCost(weight, distance)
        return baseCost * strategy.getPriorityMultiplier()
    }
}
