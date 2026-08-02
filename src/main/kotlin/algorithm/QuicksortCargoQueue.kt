package algorithm

import domain.model.Package

private const val MAX_UNSORTABLE_SIZE = 1
private const val START_INDEX = 0
private const val INDEX_OFFSET = 1

fun sortPackagesByWeightDescending(cargoQueue: MutableList<Package>) {

    if (cargoQueue.size <= MAX_UNSORTABLE_SIZE) return

    val originalIndices = IntArray(cargoQueue.size) { it }
    val state = CargoSortState(cargoQueue, originalIndices)

    quickSortRecursive(state, START_INDEX, cargoQueue.lastIndex)
}

private fun quickSortRecursive(state: CargoSortState, low: Int, high: Int) {
    if (low >= high) return

    val pivotPosition = partition(state, low, high)
    val leftPartitionEnd = pivotPosition - INDEX_OFFSET
    val rightPartitionStart = pivotPosition + INDEX_OFFSET

    quickSortRecursive(state, low, leftPartitionEnd)
    quickSortRecursive(state, rightPartitionStart, high)
}

private fun partition(state: CargoSortState, low: Int, high: Int): Int {
    val boundary = moveHeavierElementsLeft(state, low, high)

    val finalPivotPosition = boundary + INDEX_OFFSET
    swapElements(state, finalPivotPosition, high)
    return finalPivotPosition
}
private fun moveHeavierElementsLeft(state: CargoSortState, low: Int, high: Int): Int {
    val pivotWeight = state.cargoQueue[high].weight
    val pivotOriginalIndex = state.originalIndices[high]

    var boundary = low - INDEX_OFFSET

    for (current in low until high) {
        if (shouldComeBefore(state.cargoQueue[current].weight, state.originalIndices[current], pivotWeight, pivotOriginalIndex)) {
            boundary += INDEX_OFFSET
            swapElements(state, boundary, current)
        }
    }
    return boundary
}

private fun shouldComeBefore(
    weight: Double, originalIndex: Int,
    pivotWeight: Double, pivotOriginalIndex: Int
): Boolean {
    if (weight != pivotWeight) {
        return weight > pivotWeight
    }
    return originalIndex < pivotOriginalIndex
}

private fun swapElements(state: CargoSortState, indexA: Int, indexB: Int) {
    val tempPackage = state.cargoQueue[indexA]
    state.cargoQueue[indexA] = state.cargoQueue[indexB]
    state.cargoQueue[indexB] = tempPackage

    val tempIndex = state.originalIndices[indexA]
    state.originalIndices[indexA] = state.originalIndices[indexB]
    state.originalIndices[indexB] = tempIndex
}
