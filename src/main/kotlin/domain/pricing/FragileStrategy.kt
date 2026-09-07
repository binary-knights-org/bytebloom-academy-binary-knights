package domain.pricing

class FragileStrategy : DispatchStrategy {
    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return SAFETY_FEE + (weight * WEIGHT_MULTIPLIER) + (distance * DISTANCE_MULTIPLIER)
    }

    override fun getPriorityMultiplier(): Double = PRIORITY_MULTIPLIER

    private companion object {
        const val SAFETY_FEE = 15.0
        const val WEIGHT_MULTIPLIER = 1.0
        const val DISTANCE_MULTIPLIER = 1.2
        const val PRIORITY_MULTIPLIER = 1.5
    }
}
