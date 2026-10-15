package domain.algorithm.dp

import domain.model.Package

class KnapsackCargoOptimizer {
    private val optimizer1D = Knapsack1DOptimizer()
    private val optimizer2D = Knapsack2DOptimizer()

    operator fun invoke(
        packages: List<Package>,
        maxCapacityKg: Double
    ): List<Package> = optimizer1D(packages, maxCapacityKg)

    fun optimize2D(
        packages: List<Package>,
        maxCapacityKg: Double,
        maxVolumeM3: Double
    ): List<Package> = optimizer2D(packages, maxCapacityKg, maxVolumeM3)
}
