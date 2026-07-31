package domain.pricing


class EcoStrategy : DispatchStrategy {
    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return (weight * 0.5) + (distance * 1.0)
    }
    override fun getPriorityMultiplier(): Double = 1.0
    }
