package algorithm

import domain.model.Package

fun sortPackagesByWeightDescending(cargoQueue: MutableList<Package>) {
    val maxUnsortableSize = 1
    if (cargoQueue.size <= maxUnsortableSize) return

    val startIndex = 0
    val originalIndices = IntArray(cargoQueue.size) { it }

    quickSortRecursive(cargoQueue, originalIndices, startIndex, cargoQueue.lastIndex)
}

private fun quickSortRecursive(cargoQueue: MutableList<Package>, originalIndices: IntArray, low: Int, high: Int) {
    if (low >= high) return

    val pivotPosition = partition(cargoQueue, originalIndices, low, high)

    val indexOffset = 1
    val leftPartitionEnd = pivotPosition - indexOffset
    val rightPartitionStart = pivotPosition + indexOffset

    quickSortRecursive(cargoQueue, originalIndices, low, leftPartitionEnd)
    quickSortRecursive(cargoQueue, originalIndices, rightPartitionStart, high)
}

private fun partition(cargoQueue: MutableList<Package>, originalIndices: IntArray, low: Int, high: Int): Int {
    val pivotWeight = cargoQueue[high].weight
    val pivotOriginalIndex = originalIndices[high]

    val indexOffset = 1
    var boundary = low - indexOffset

    for (current in low until high) {
        if (shouldComeBefore(cargoQueue[current].weight, originalIndices[current], pivotWeight, pivotOriginalIndex)) {
            boundary += indexOffset
            swapElements(cargoQueue, originalIndices, boundary, current)
        }
    }

    val finalPivotPosition = boundary + indexOffset
    swapElements(cargoQueue, originalIndices, finalPivotPosition, high)

    return finalPivotPosition
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

private fun swapElements(cargoQueue: MutableList<Package>, originalIndices: IntArray, indexA: Int, indexB: Int) {
    val tempPackage = cargoQueue[indexA]
    cargoQueue[indexA] = cargoQueue[indexB]
    cargoQueue[indexB] = tempPackage

    val tempIndex = originalIndices[indexA]
    originalIndices[indexA] = originalIndices[indexB]
    originalIndices[indexB] = tempIndex
}
