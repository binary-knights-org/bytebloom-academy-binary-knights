package domain.algorithm

import domain.model.Package

private const val MAX_UNSORTABLE_SIZE = 1
private const val START_INDEX = 0
private const val INDEX_OFFSET = 1

fun sortPackagesDescendingByWeight(cargoQueue: MutableList<Package>) {

    if (cargoQueue.size <= MAX_UNSORTABLE_SIZE) return

    val originalIndices = IntArray(cargoQueue.size) { it }
    val state = CargoSortState(cargoQueue, originalIndices)

    quickSortRecursive(state, START_INDEX, cargoQueue.lastIndex)
}

private fun quickSortRecursive(state: CargoSortState, low: Int, high: Int) {
    if (low >= high) return

    val pivotPosition = rearrangePackagesAroundPivot(state, low, high)
    val leftPartitionEnd = pivotPosition - INDEX_OFFSET
    val rightPartitionStart = pivotPosition + INDEX_OFFSET

    quickSortRecursive(state, low, leftPartitionEnd)
    quickSortRecursive(state, rightPartitionStart, high)
}

private fun rearrangePackagesAroundPivot(state: CargoSortState, low: Int, high: Int): Int {
    val lastPositionOfHeavierElements  = moveHeavierElementsLeft(state, low, high)

    val finalPivotPosition = lastPositionOfHeavierElements + INDEX_OFFSET
    swapPackages(state, finalPivotPosition, high)
    return finalPivotPosition
}
private fun moveHeavierElementsLeft(state: CargoSortState, low: Int, high: Int): Int {
    var lastHeavierPackageIndex = low - INDEX_OFFSET

    for (current in low until high) {
        if (isHeavierWeight(state, current, high)) {
            lastHeavierPackageIndex += INDEX_OFFSET
            swapPackages(state, lastHeavierPackageIndex, current)
        }
    }
    return lastHeavierPackageIndex
}

private fun isHeavierWeight(
    state: CargoSortState, currentIndex: Int, pivotIndex: Int): Boolean {
    val weight = state.cargoQueue[currentIndex].weight
    val pivotWeight = state.cargoQueue[pivotIndex].weight

    if (weight != pivotWeight) {
        return weight > pivotWeight
    }
    return state.originalIndices[currentIndex] < state.originalIndices[pivotIndex]
}

private fun swapPackages(state: CargoSortState, firstIndex: Int, secondIndex: Int) {
    val tempPackage = state.cargoQueue[firstIndex]
    state.cargoQueue[firstIndex] = state.cargoQueue[secondIndex]
    state.cargoQueue[secondIndex] = tempPackage

    val tempIndex = state.originalIndices[firstIndex]
    state.originalIndices[firstIndex] = state.originalIndices[secondIndex]
    state.originalIndices[secondIndex] = tempIndex
}
