package domain.algorithm.dp

import domain.model.Package
import domain.model.Priority
import kotlin.math.roundToInt

class KnapsackCargoOptimizer {

    fun optimize(
        packages: List<Package>,
        maxCapacityKg: Double
    ): List<Package> {
        if (packages.isEmpty() || maxCapacityKg <= 0.0) {
            return emptyList()
        }

        val capacity = toCapacityUnits(maxCapacityKg)
        val weights = packages.map { toCapacityUnits(it.weight) }
        val priorities = packages.map { priorityValue(it.priority) }

        val dp = Array(packages.size + 1) {
            IntArray(capacity + 1)
        }

        for (i in 1..packages.size) {
            val packageWeight = weights[i - 1]
            val packagePriority = priorities[i - 1]

            for (currentCapacity in 0..capacity) {
                dp[i][currentCapacity] = dp[i - 1][currentCapacity]

                if (packageWeight <= currentCapacity) {
                    val priorityWithPackage =
                        dp[i - 1][currentCapacity - packageWeight] + packagePriority

                    if (priorityWithPackage > dp[i][currentCapacity]) {
                        dp[i][currentCapacity] = priorityWithPackage
                    }
                }
            }
        }

        return recoverSelectedPackages(
            packages = packages,
            weights = weights,
            dp = dp,
            capacity = capacity
        )
    }

    private fun recoverSelectedPackages(
        packages: List<Package>,
        weights: List<Int>,
        dp: Array<IntArray>,
        capacity: Int
    ): List<Package> {
        val selectedPackages = mutableListOf<Package>()
        var remainingCapacity = capacity

        for (i in packages.size downTo 1) {
            if (dp[i][remainingCapacity] != dp[i - 1][remainingCapacity]) {
                selectedPackages.add(packages[i - 1])
                remainingCapacity -= weights[i - 1]
            }
        }

        return selectedPackages.asReversed()
    }

    private fun priorityValue(priority: Priority): Int =
        when (priority) {
            Priority.URGENT -> 3
            Priority.STANDARD -> 2
            Priority.LOW -> 1
        }

    private fun toCapacityUnits(weightKg: Double): Int =
        (weightKg * CAPACITY_SCALE).roundToInt()

    companion object {
        private const val CAPACITY_SCALE = 1000
    }
}
