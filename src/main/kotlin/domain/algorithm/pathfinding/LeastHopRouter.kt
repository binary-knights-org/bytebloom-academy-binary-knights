package domain.algorithm.pathfinding

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class LeastHopRouter(
    warehouseRepository: WarehouseRepository
) : BaseShortestPathRouter(warehouseRepository) {

    override fun executeSearch(
        origin: Warehouse,
        destination: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse>? {
        val state = initializeState(origin)
        val previousWarehouseOf = exploreBreadthFirst(destination, state) ?: return null
        visitedWarehouseCount = state.visitedWarehouseIds.size
        return reconstructPath(destination, previousWarehouseOf)
    }

    private fun initializeState(origin: Warehouse): BfsState =
        BfsState().apply {
            queue.addLast(origin)
            visitedWarehouseIds.add(origin.id)
        }

    private fun exploreBreadthFirst(
        destination: Warehouse,
        state: BfsState
    ): Map<String, Warehouse>? {
        while (state.queue.isNotEmpty()) {
            val current = state.queue.removeFirst()

            if (current.id == destination.id) {
                return state.previousWarehouseOf
            }

            enqueueUnvisitedNeighbors(current, state)
        }
        return null
    }

    private fun enqueueUnvisitedNeighbors(current: Warehouse, state: BfsState) {
        current.outgoingRoutes.forEach { route ->
            enqueueNeighborIfUnvisited(route.destinationHub, current, state)
        }
    }

    private fun enqueueNeighborIfUnvisited(
        neighbor: Warehouse,
        current: Warehouse,
        state: BfsState
    ) {
        if (state.visitedWarehouseIds.add(neighbor.id)) {
            state.previousWarehouseOf[neighbor.id] = current
            state.queue.addLast(neighbor)
        }
    }
}
