package domain.algorithm.sorting

import domain.model.Package

data class CargoSortState(
    val cargoQueue: MutableList<Package>,
    val originalIndices: IntArray
) {
    fun swap(firstIndex: Int, secondIndex: Int) {
        if (firstIndex == secondIndex) return

        val tempPackage = cargoQueue[firstIndex]
        cargoQueue[firstIndex] = cargoQueue[secondIndex]
        cargoQueue[secondIndex] = tempPackage

        val tempIndex = originalIndices[firstIndex]
        originalIndices[firstIndex] = originalIndices[secondIndex]
        originalIndices[secondIndex] = tempIndex
    }
}
