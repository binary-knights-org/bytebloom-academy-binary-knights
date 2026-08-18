package domain.algorithm.pathfinding

import domain.model.Warehouse

internal fun reconstructPath(
    destination: Warehouse,
    previousWarehouseOf: Map<String, Warehouse>
): List<Warehouse> {
    val path = mutableListOf<Warehouse>()
    var current: Warehouse? = destination

    while (current != null) {
        path.add(current)
        current = previousWarehouseOf[current.id]
    }
    return path.reversed()
}