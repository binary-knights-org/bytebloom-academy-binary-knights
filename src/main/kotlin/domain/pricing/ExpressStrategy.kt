package domain.pricing


private const val WEIGHT_MULTIPLIER = 2.5
private const val DISTANCE_MULTIPLIER = 3.0
private const val PRIORITY_MULTIPLIER = 2.0

class ExpressStrategy : DispatchStrategy {
    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return (weight * WEIGHT_MULTIPLIER) + (distance * DISTANCE_MULTIPLIER)
    }
    override fun getPriorityMultiplier(): Double = PRIORITY_MULTIPLIER
}
