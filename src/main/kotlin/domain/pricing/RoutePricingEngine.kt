package domain.pricing

class RoutePricingEngine(private var strategy: DispatchStrategy) {
    fun setStrategy(newStrategy: DispatchStrategy) {
        this.strategy = newStrategy
    }
    fun calculateCost(weight: Double, distance: Double): Double {
        return strategy.calculateTransitCost(weight, distance)
    }
    fun getPriority(): Double {
        return strategy.getPriorityMultiplier()
    }
}
