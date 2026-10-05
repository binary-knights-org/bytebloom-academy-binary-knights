package domain.algorithm.dp

import domain.model.Package
import domain.model.Priority
import domain.model.exception.InvalidCargoInputException
import kotlin.math.roundToInt

class KnapsackCargoOptimizer {

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

    private fun priorityValue(priority: Priority): Int =
        when (priority) {
            Priority.URGENT -> URGENT_PRIORITY_VALUE
            Priority.STANDARD -> STANDARD_PRIORITY_VALUE
            Priority.LOW -> LOW_PRIORITY_VALUE
        }

    private fun toCapacityUnits(weightKg: Double): Int =
        (weightKg * CAPACITY_SCALE).roundToInt()

    companion object {
        private const val GRID_SIZE_OFFSET = 1
        private const val CAPACITY_SCALE = 1000

        private const val MIN_CAPACITY_KG = 0.0
        private const val MIN_PACKAGE_WEIGHT_KG = 0.0

        private const val URGENT_PRIORITY_VALUE = 3
        private const val STANDARD_PRIORITY_VALUE = 2
        private const val LOW_PRIORITY_VALUE = 1
    }
}
