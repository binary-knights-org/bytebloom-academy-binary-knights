package domain.algorithm
import domain.model.Package

data class CargoSortState(
    val cargoQueue: MutableList<Package>,
    val originalIndices: IntArray,
)
