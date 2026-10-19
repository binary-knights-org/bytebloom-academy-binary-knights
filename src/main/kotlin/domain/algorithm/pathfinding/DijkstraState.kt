package domain.algorithm.pathfinding

import domain.model.Warehouse

class DijkstraState(startWarehouse: Warehouse) {
    val distances: MutableMap<String, Double> = mutableMapOf(startWarehouse.id to INITIAL_DISTANCE)
    val previousWarehouseOf: MutableMap<String, Warehouse> = mutableMapOf()
    val unvisitedWarehouses: MutableSet<Warehouse> = mutableSetOf(startWarehouse)
    val visitedWarehouseIds: MutableSet<String> = mutableSetOf()

    private companion object {
        const val INITIAL_DISTANCE = 0.0
    }
}
