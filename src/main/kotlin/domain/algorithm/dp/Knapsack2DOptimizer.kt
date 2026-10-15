package domain.algorithm.dp

import domain.model.Package
import domain.model.exception.InvalidCargoInputException

class Knapsack2DOptimizer : BaseKnapsackOptimizer() {

    operator fun invoke(
        packages: List<Package>,
        maxCapacityKg: Double,
        maxVolumeM3: Double
    ): List<Package> {
        validate2DInputs(packages, maxCapacityKg, maxVolumeM3)

        val capacities = CapacityLimits(
            weight = toCapacityUnits(maxCapacityKg),
            volume = toVolumeUnits(maxVolumeM3)
        )

        val valueGrid = build2DValueGrid(packages, capacities)
        return recover2DSelectedPackages(packages, valueGrid, capacities)
    }

    private fun validate2DInputs(packages: List<Package>, maxCapacityKg: Double, maxVolumeM3: Double) {
        if (!isValidWeightCapacity(maxCapacityKg) || !isValidVolumeCapacity(maxVolumeM3)) {
            throw InvalidCargoInputException()
        }
        if (packages.any { !isValid2DPackage(it) }) {
            throw InvalidCargoInputException()
        }
    }

    private fun isValidWeightCapacity(capacity: Double): Boolean =
        capacity.isFinite() && capacity >= MIN_CAPACITY_KG

    private fun isValidVolumeCapacity(volume: Double): Boolean =
        volume.isFinite() && volume >= MIN_VOLUME_M3

    private fun isValid2DPackage(pkg: Package): Boolean =
        isValidPackageWeight(pkg.weight) && isValidPackageVolume(pkg.volumeM3)

    private fun isValidPackageWeight(weight: Double): Boolean =
        weight.isFinite() && weight >= MIN_PACKAGE_WEIGHT_KG

    private fun isValidPackageVolume(volume: Double?): Boolean =
        volume != null && volume.isFinite() && volume >= MIN_VOLUME_M3

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
            fill2DGridRow(grid, packages, packageIndex, capacities)
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
        val packageVolume = currentPackage.volumeM3?.let(::toVolumeUnits)
            ?: throw InvalidCargoInputException()
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
                currentRow[currentWeight][currentVolume] = calculate2DBestValue(previousRow, state)
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
        val valueWithPackage = previousRow[state.weight - state.packageWeight][
            state.volume - state.packageVolume] + state.packageValue

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
            if (is2DPackageSelected(valueGrid, packageIndex, remainingWeight, remainingVolume)) {
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
}
