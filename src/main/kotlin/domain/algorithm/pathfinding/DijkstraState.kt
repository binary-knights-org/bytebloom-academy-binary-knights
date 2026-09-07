package domain.algorithm.pathfinding

import domain.model.Warehouse

class DijkstraState(startWarehouse: Warehouse) {
    val distances = mutableMapOf(startWarehouse.id to INITIAL_DISTANCE)
    val previousWarehouseOf = mutableMapOf<String , Warehouse>()
    val unvisitedWarehouses = mutableSetOf(startWarehouse)
    val visitedWarehouseIds = mutableSetOf<String>()

    private companion object {
        const val INITIAL_DISTANCE = 0.0
    }
}
