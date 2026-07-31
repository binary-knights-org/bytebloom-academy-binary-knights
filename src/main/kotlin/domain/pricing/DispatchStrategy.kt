package domain.pricing

interface DispatchStrategy {
    fun calculateTransitCost(weight: Double, distance: Double): Double
    fun getPriorityMultiplier(): Double
}