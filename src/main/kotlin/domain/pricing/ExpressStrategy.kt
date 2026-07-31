package domain.pricing

class ExpressStrategy : DispatchStrategy {
    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return (weight * 2.5) + (distance * 3.0)
    }

    override fun getPriorityMultiplier(): Double = 2.0
}