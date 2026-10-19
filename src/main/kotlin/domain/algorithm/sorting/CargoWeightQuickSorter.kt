package domain.algorithm.sorting

import domain.model.Package

class CargoWeightQuickSorter {

    fun sortDescendingByWeight(cargoQueue: MutableList<Package>) {
        if (cargoQueue.size <= 1) return

        val originalIndices = IntArray(cargoQueue.size) { it }
        val state = CargoSortState(cargoQueue, originalIndices)

        quickSortRecursive(state, low = 0, high = cargoQueue.lastIndex)
    }

    private fun quickSortRecursive(state: CargoSortState, low: Int, high: Int) {
        if (low >= high) return

        val pivotPosition = rearrangePackagesAroundPivot(state, low, high)

        quickSortRecursive(state, low, pivotPosition - 1)
        quickSortRecursive(state, pivotPosition + 1, high)
    }

    private fun rearrangePackagesAroundPivot(state: CargoSortState, low: Int, high: Int): Int {
        var pivotIndex = low

        for (current in low until high) {
            if (isHeavierWeight(state, current, high)) {
                state.swap(pivotIndex, current)
                pivotIndex++
            }
        }

        state.swap(pivotIndex, high)
        return pivotIndex
    }

    private fun isHeavierWeight(state: CargoSortState, currentIndex: Int, pivotIndex: Int): Boolean {
        val currentWeight = state.cargoQueue[currentIndex].weight
        val pivotWeight = state.cargoQueue[pivotIndex].weight

        return if (currentWeight != pivotWeight) {
            currentWeight > pivotWeight
        } else {
            state.originalIndices[currentIndex] < state.originalIndices[pivotIndex]
        }
    }
}
