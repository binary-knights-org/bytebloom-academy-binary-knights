package domain.algorithm.pathfinding

import domain.model.Warehouse

class BfsState {
    val queue: ArrayDeque<Warehouse> = ArrayDeque()
    val visitedWarehouseIds: MutableSet<String> = mutableSetOf()
    val previousWarehouseOf: MutableMap<String, Warehouse> = mutableMapOf()
}
