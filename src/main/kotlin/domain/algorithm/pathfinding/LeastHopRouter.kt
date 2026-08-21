package domain.algorithm.pathfinding

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class LeastHopRouter(
    private val warehouseRepository: WarehouseRepository
) : ShortestPathRouter {

    var visitedWarehouseCount: Int = 0
        private set

    override fun findShortestPath(
        origin: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {
        visitedWarehouseCount = 0

        val allWarehouses = warehouseRepository.getAllWarehouses()
        val warehousesById = allWarehouses.associateBy { it.id }

        val actualOrigin = warehousesById[origin.id]
        val actualDestination = warehousesById[destination.id]

        val path = when {
            actualOrigin == null || actualDestination == null -> null

            actualOrigin.id == actualDestination.id -> {
                visitedWarehouseCount = 1
                listOf(actualOrigin)
            }

            else -> executeSearch(
                actualOrigin,
                actualDestination
            )
        }

        return path
    }

    private fun executeSearch(
        origin: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {

        val state = initializeState(origin)
        val previousWarehouseOf = exploreBreadthFirst(destination, state) ?: return null
        visitedWarehouseCount = state.visitedWarehouseIds.size
        return reconstructPath(destination, previousWarehouseOf)
    }

    private fun initializeState(
        origin: Warehouse
    ): BfsState {
        return BfsState().apply {
            queue.addLast(origin)
            visitedWarehouseIds.add(origin.id)
        }
    }

    private fun exploreBreadthFirst(
        destination: Warehouse,
        state: BfsState
    ): Map<String, Warehouse>? {

        while (state.queue.isNotEmpty()) {
            val current = state.queue.removeFirst()

            if (hasReachedDestination(current, destination)) {
                return state.previousWarehouseOf
            }

            enqueueUnvisitedNeighbors(current, state)
        }

        return null
    }

    private fun enqueueUnvisitedNeighbors(
        current: Warehouse,
        state: BfsState
    ) {
        current.outgoingRoutes.forEach { route ->
            enqueueNeighborIfUnvisited(route.destinationHub, current, state)
        }
    }

    private fun enqueueNeighborIfUnvisited(
        neighbor: Warehouse,
        current: Warehouse,
        state: BfsState
    ) {
        if (!state.visitedWarehouseIds.add(neighbor.id)) {
            return
        }
        state.previousWarehouseOf[neighbor.id] = current
        state.queue.addLast(neighbor)
    }

    private fun hasReachedDestination(current: Warehouse, destination: Warehouse): Boolean =
        current.id == destination.id
}
