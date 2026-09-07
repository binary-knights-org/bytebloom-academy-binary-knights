package domain.pricing




class ExpressStrategy : DispatchStrategy {
    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return (weight * WEIGHT_MULTIPLIER) + (distance * DISTANCE_MULTIPLIER)
    }
    override fun getPriorityMultiplier(): Double = PRIORITY_MULTIPLIER

    private companion object {
        const val WEIGHT_MULTIPLIER = 2.5
        const val DISTANCE_MULTIPLIER = 3.0
        const val PRIORITY_MULTIPLIER = 2.0
    }
}
