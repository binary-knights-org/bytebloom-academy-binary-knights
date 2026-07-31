package domain.pricing

private const val SAFETY_FEE = 15.0
private const val WEIGHT_MULTIPLIER = 1.0
private const val DISTANCE_MULTIPLIER = 1.2
private const val PRIORITY_MULTIPLIER = 1.5

class FragileStrategy : DispatchStrategy {
    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return SAFETY_FEE + (weight * WEIGHT_MULTIPLIER) + (distance * DISTANCE_MULTIPLIER)
    }

    override fun getPriorityMultiplier(): Double = PRIORITY_MULTIPLIER
}
