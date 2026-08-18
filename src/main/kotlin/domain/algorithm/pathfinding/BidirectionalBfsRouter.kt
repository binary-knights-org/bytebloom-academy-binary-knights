package domain.algorithm.pathfinding

import domain.model.Warehouse

class BidirectionalBfsRouter : ShortestPathRouter {
    override fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        if (origin == destination) return listOf(origin)
//        val forwardState = createInitialState(origin)
//        val backwardState = createInitialState(destination)
        return null
    }

    private fun createInitialState(startWarehouse: Warehouse): BfsState {
        return BfsState().apply {
            queue.add(startWarehouse)
            visitedWarehouseIds.add(startWarehouse.id)
        }
    }
}
