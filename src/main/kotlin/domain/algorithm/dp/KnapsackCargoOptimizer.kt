package domain.algorithm.dp

import domain.model.Package
import domain.model.Priority
import domain.model.exception.InvalidCargoInputException
import kotlin.math.roundToInt

class KnapsackCargoOptimizer {

    // 1D Knapsack - Existing

    operator fun invoke(
        packages: List<Package>,
        maxCapacityKg: Double
    ): List<Package> {
        validateInputs(packages, maxCapacityKg)

        val capacity = toCapacityUnits(maxCapacityKg)
        val valueGrid = buildValueGrid(packages, capacity)

        return recoverSelectedPackages(
            packages = packages,
            valueGrid = valueGrid,
            capacity = capacity
        )
    }

    private fun validateInputs(
        packages: List<Package>,
        maxCapacityKg: Double
    ) {
        if (maxCapacityKg < MIN_CAPACITY_KG) { throw InvalidCargoInputException() }
        if (packages.any { it.weight < MIN_PACKAGE_WEIGHT_KG }) { throw InvalidCargoInputException() }
    }

    private fun buildValueGrid(
        packages: List<Package>,
        capacity: Int
    ): Array<IntArray> {
        val grid = Array(packages.size + GRID_SIZE_OFFSET) {
            IntArray(capacity + GRID_SIZE_OFFSET)
        }

        for (packageIndex in packages.indices) {
            fillGridRow(
                grid = grid,
                packages = packages,
                packageIndex = packageIndex,
                capacity = capacity
            )
        }

        return grid
    }

    private fun fillGridRow(
        grid: Array<IntArray>,
        packages: List<Package>,
        packageIndex: Int,
        capacity: Int
    ) {
        val currentPackage = packages[packageIndex]
        val packageWeight = toCapacityUnits(currentPackage.weight)
        val packageValue = priorityValue(currentPackage.priority) * packageWeight

        for (currentCapacity in 0..capacity) {
            grid[packageIndex + GRID_SIZE_OFFSET][currentCapacity] =
                calculateBestValue(
                    previousRow = grid[packageIndex],
                    currentCapacity = currentCapacity,
                    packageWeight = packageWeight,
                    packagePriority = packageValue
                )
        }
    }

    private fun calculateBestValue(
        previousRow: IntArray,
        currentCapacity: Int,
        packageWeight: Int,
        packagePriority: Int
    ): Int {
        if (packageWeight > currentCapacity) {
            return previousRow[currentCapacity]
        }

        val valueWithoutPackage = previousRow[currentCapacity]
        val valueWithPackage =
            previousRow[currentCapacity - packageWeight] + packagePriority

        return maxOf(valueWithoutPackage, valueWithPackage)
    }

    private fun recoverSelectedPackages(
        packages: List<Package>,
        valueGrid: Array<IntArray>,
        capacity: Int
    ): List<Package> {
        val selectedPackages = mutableListOf<Package>()
        var remainingCapacity = capacity

        for (packageIndex in packages.indices.reversed()) {
            if (isPackageSelected(valueGrid, packageIndex, remainingCapacity)) {
                val selectedPackage = packages[packageIndex]

                selectedPackages.add(selectedPackage)
                remainingCapacity -= toCapacityUnits(selectedPackage.weight)
            }
        }

        return selectedPackages.asReversed()
    }

    private fun isPackageSelected(
        valueGrid: Array<IntArray>,
        packageIndex: Int,
        remainingCapacity: Int
    ): Boolean =
        valueGrid[packageIndex + GRID_SIZE_OFFSET][remainingCapacity] !=
                valueGrid[packageIndex][remainingCapacity]

    // 2D Knapsack - Bonus

    fun optimize2D(
        packages: List<Package>,
        maxCapacityKg: Double,
        maxVolumeM3: Double
    ): List<Package> {
        validate2DInputs(
            packages = packages,
            maxCapacityKg = maxCapacityKg,
            maxVolumeM3 = maxVolumeM3
        )

        val capacities = CapacityLimits(
            weight = toCapacityUnits(maxCapacityKg),
            volume = toVolumeUnits(maxVolumeM3)
        )

        val valueGrid = build2DValueGrid(
            packages = packages,
            capacities = capacities
        )

        return recover2DSelectedPackages(
            packages = packages,
            valueGrid = valueGrid,
            capacities = capacities
        )
    }

    private fun validate2DInputs(
        packages: List<Package>,
        maxCapacityKg: Double,
        maxVolumeM3: Double
    ) {
        if (
            maxCapacityKg < MIN_CAPACITY_KG ||
            maxVolumeM3 < MIN_VOLUME_M3
        ) { throw InvalidCargoInputException() }

        if (packages.any { it.volumeM3 == null }) { throw InvalidCargoInputException() }
    }

    private fun build2DValueGrid(
        packages: List<Package>,
        capacities: CapacityLimits
    ): Array<Array<IntArray>> {
        val grid = Array(packages.size + GRID_SIZE_OFFSET) {
            Array(capacities.weight + GRID_SIZE_OFFSET) {
                IntArray(capacities.volume + GRID_SIZE_OFFSET)
            }
        }

        for (packageIndex in packages.indices) {
            fill2DGridRow(
                grid = grid,
                packages = packages,
                packageIndex = packageIndex,
                capacities = capacities
            )
        }

        return grid
    }

    private fun fill2DGridRow(
        grid: Array<Array<IntArray>>,
        packages: List<Package>,
        packageIndex: Int,
        capacities: CapacityLimits
    ) {
        val currentPackage = packages[packageIndex]
        val packageWeight = toCapacityUnits(currentPackage.weight)
        val packageVolume = currentPackage.volumeM3?.let(::toVolumeUnits) ?: throw InvalidCargoInputException()
        val packageValue = priorityValue(currentPackage.priority) * packageWeight

        val currentRow = grid[packageIndex + GRID_SIZE_OFFSET]
        val previousRow = grid[packageIndex]

        for (currentWeight in 0..capacities.weight) {
            for (currentVolume in 0..capacities.volume) {
                val state = TwoDimensionalState(
                    weight = currentWeight,
                    volume = currentVolume,
                    packageWeight = packageWeight,
                    packageVolume = packageVolume,
                    packageValue = packageValue
                )

                currentRow[currentWeight][currentVolume] =
                    calculate2DBestValue(
                        previousRow = previousRow,
                        state = state
                    )
            }
        }
    }

    private fun calculate2DBestValue(
        previousRow: Array<IntArray>,
        state: TwoDimensionalState
    ): Int {
        if (state.packageWeight > state.weight || state.packageVolume > state.volume) {
            return previousRow[state.weight][state.volume]
        }

        val valueWithoutPackage = previousRow[state.weight][state.volume]
        val valueWithPackage =
            previousRow[state.weight - state.packageWeight][state.volume - state.packageVolume] + state.packageValue

        return maxOf(valueWithoutPackage, valueWithPackage)
    }

    private fun recover2DSelectedPackages(
        packages: List<Package>,
        valueGrid: Array<Array<IntArray>>,
        capacities: CapacityLimits
    ): List<Package> {
        val selectedPackages = mutableListOf<Package>()

        var remainingWeight = capacities.weight
        var remainingVolume = capacities.volume

        for (packageIndex in packages.indices.reversed()) {
            if (
                is2DPackageSelected(
                    valueGrid = valueGrid,
                    packageIndex = packageIndex,
                    remainingWeight = remainingWeight,
                    remainingVolume = remainingVolume
                )
            ) {
                val selectedPackage = packages[packageIndex]

                selectedPackages.add(selectedPackage)

                remainingWeight -= toCapacityUnits(selectedPackage.weight)

                val packageVolume = selectedPackage.volumeM3?.let(::toVolumeUnits)
                    ?: throw InvalidCargoInputException()

                remainingVolume -= packageVolume
            }
        }

        return selectedPackages.asReversed()
    }

    private fun is2DPackageSelected(
        valueGrid: Array<Array<IntArray>>,
        packageIndex: Int,
        remainingWeight: Int,
        remainingVolume: Int
    ): Boolean {
        val currentRow = valueGrid[packageIndex + GRID_SIZE_OFFSET]
        val previousRow = valueGrid[packageIndex]

        return currentRow[remainingWeight][remainingVolume] != previousRow[remainingWeight][remainingVolume]
    }

    private fun priorityValue(priority: Priority): Int =
        when (priority) {
            Priority.URGENT -> URGENT_PRIORITY_VALUE
            Priority.STANDARD -> STANDARD_PRIORITY_VALUE
            Priority.LOW -> LOW_PRIORITY_VALUE
        }

    private fun toCapacityUnits(weightKg: Double): Int =
        (weightKg * CAPACITY_SCALE).roundToInt()

    private fun toVolumeUnits(volumeM3: Double): Int =
        (volumeM3 * VOLUME_SCALE).roundToInt()

    private data class CapacityLimits(
        val weight: Int,
        val volume: Int
    )

    private data class TwoDimensionalState(
        val weight: Int,
        val volume: Int,
        val packageWeight: Int,
        val packageVolume: Int,
        val packageValue: Int
    )

    companion object {
        private const val GRID_SIZE_OFFSET = 1

        private const val CAPACITY_SCALE = 1000
        private const val VOLUME_SCALE = 10

        private const val MIN_CAPACITY_KG = 0.0
        private const val MIN_VOLUME_M3 = 0.0

        private const val MIN_PACKAGE_WEIGHT_KG = 0.0

        private const val URGENT_PRIORITY_VALUE = 3
        private const val STANDARD_PRIORITY_VALUE = 2
        private const val LOW_PRIORITY_VALUE = 1
    }
}
