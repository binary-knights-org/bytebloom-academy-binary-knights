package domain.algorithm.pathfinding

import domain.model.Warehouse

private const val INITIAL_DISTANCE = 0.0

class DijkstraState(startWarehouse: Warehouse) {
    val distances = mutableMapOf(startWarehouse.id to INITIAL_DISTANCE)
    val previousWarehouseOf = mutableMapOf<String , Warehouse>()
    val unvisitedWarehouses = mutableSetOf<Warehouse>(startWarehouse)
    val visitedWarehouseIds = mutableSetOf<String>()
}
