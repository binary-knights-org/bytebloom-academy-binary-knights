package domain.algorithm.dp

import domain.model.Package
import domain.model.exception.InvalidCargoInputException

class Knapsack1DOptimizer : BaseKnapsackOptimizer() {

    operator fun invoke(
        packages: List<Package>,
        maxCapacityKg: Double
    ): List<Package> {
        validateInputs(packages, maxCapacityKg)

        val capacity = toCapacityUnits(maxCapacityKg)
        val valueGrid = buildValueGrid(packages, capacity)

        return recoverSelectedPackages(packages, valueGrid, capacity)
    }

    private fun validateInputs(packages: List<Package>, maxCapacityKg: Double) {
        if (!maxCapacityKg.isFinite() || maxCapacityKg < MIN_CAPACITY_KG) {
            throw InvalidCargoInputException()
        }
        if (packages.any { !it.weight.isFinite() || it.weight < MIN_PACKAGE_WEIGHT_KG }) {
            throw InvalidCargoInputException()
        }
    }

    private fun buildValueGrid(packages: List<Package>, capacity: Int): Array<IntArray> {
        val grid = Array(packages.size + GRID_SIZE_OFFSET) {
            IntArray(capacity + GRID_SIZE_OFFSET)
        }

        for (packageIndex in packages.indices) {
            fillGridRow(grid, packages, packageIndex, capacity)
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
        val packageValue = calculatePackageValue(currentPackage)

        for (currentCapacity in 0..capacity) {
            grid[packageIndex + GRID_SIZE_OFFSET][currentCapacity] = calculateBestValue(
                previousRow = grid[packageIndex],
                currentCapacity = currentCapacity,
                packageWeight = packageWeight,
                packageValue = packageValue
            )
        }
    }

    private fun calculateBestValue(
        previousRow: IntArray,
        currentCapacity: Int,
        packageWeight: Int,
        packageValue: Int
    ): Int {
        if (packageWeight > currentCapacity) return previousRow[currentCapacity]

        val valueWithoutPackage = previousRow[currentCapacity]
        val valueWithPackage = previousRow[currentCapacity - packageWeight] + packageValue

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
    ): Boolean = valueGrid[packageIndex + GRID_SIZE_OFFSET][remainingCapacity] !=
            valueGrid[packageIndex][remainingCapacity]
}
