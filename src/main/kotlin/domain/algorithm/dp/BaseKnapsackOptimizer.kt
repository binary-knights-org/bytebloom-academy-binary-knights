package domain.algorithm.dp

import domain.model.Package
import domain.model.Priority
import kotlin.math.roundToInt

abstract class BaseKnapsackOptimizer {

    protected fun calculatePackageValue(pkg: Package): Int =
        priorityValue(pkg.priority) * toCapacityUnits(pkg.weight)

    protected fun priorityValue(priority: Priority): Int = when (priority) {
        Priority.URGENT -> URGENT_PRIORITY_VALUE
        Priority.STANDARD -> STANDARD_PRIORITY_VALUE
        Priority.LOW -> LOW_PRIORITY_VALUE
    }

    protected fun toCapacityUnits(weightKg: Double): Int =
        (weightKg * CAPACITY_SCALE).roundToInt()

    protected fun toVolumeUnits(volumeM3: Double): Int =
        (volumeM3 * VOLUME_SCALE).roundToInt()

    protected data class CapacityLimits(val weight: Int, val volume: Int)

    protected data class TwoDimensionalState(
        val weight: Int,
        val volume: Int,
        val packageWeight: Int,
        val packageVolume: Int,
        val packageValue: Int
    )

    companion object {
        protected const val GRID_SIZE_OFFSET = 1
        protected const val CAPACITY_SCALE = 1000
        protected const val VOLUME_SCALE = 10

        protected const val MIN_CAPACITY_KG = 0.0
        protected const val MIN_VOLUME_M3 = 0.0
        protected const val MIN_PACKAGE_WEIGHT_KG = 0.0

        private const val URGENT_PRIORITY_VALUE = 3
        private const val STANDARD_PRIORITY_VALUE = 2
        private const val LOW_PRIORITY_VALUE = 1
    }
}
