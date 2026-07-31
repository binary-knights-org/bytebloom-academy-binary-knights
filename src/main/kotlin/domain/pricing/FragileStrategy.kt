package domain.pricing

class FragileStrategy : DispatchStrategy {
    private val safetyFee: Double = 15.0

    override fun calculateTransitCost(weight: Double, distance: Double): Double {
        return safetyFee + (weight * 1.0) + (distance * 1.2)
    }
    override fun getPriorityMultiplier(): Double = 1.5
}
