package algorithm

import domain.model.Package

fun sortPackagesByWeightDescending(cargoQueue: MutableList<Package>) {
    if (cargoQueue.size <= 1) return

    val originalIndices = IntArray(cargoQueue.size) { it }
    quickSortRecursive(cargoQueue, originalIndices, 0, cargoQueue.size - 1)
}

private fun quickSortRecursive(cargoQueue: MutableList<Package>, originalIndices: IntArray, low: Int, high: Int) {
    if (low >= high) return

    val pivotPosition = partition(cargoQueue, originalIndices, low, high)
    quickSortRecursive(cargoQueue, originalIndices, low, pivotPosition - 1)
    quickSortRecursive(cargoQueue, originalIndices, pivotPosition + 1, high)
}

private fun partition(cargoQueue: MutableList<Package>, originalIndices: IntArray, low: Int, high: Int): Int {
    val pivotWeight = cargoQueue[high].weight
    val pivotOriginalIndex = originalIndices[high]
    var boundary = low - 1

    for (current in low until high) {
        if (shouldComeBefore(cargoQueue[current].weight, originalIndices[current], pivotWeight, pivotOriginalIndex)) {
            boundary++
            swapElements(cargoQueue, originalIndices, boundary, current)
        }
    }

    swapElements(cargoQueue, originalIndices, boundary + 1, high)
    return boundary + 1
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
