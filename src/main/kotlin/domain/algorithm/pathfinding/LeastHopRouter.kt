package domain.algorithm.pathfinding

import domain.model.Warehouse

class LeastHopRouter : ShortestPathRouter {

    var visitedWarehouseCount: Int = 0
        private set

    override fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        if (hasReachedDestination(origin, destination)) return listOf(origin)
        return executeSearch(origin, destination)
    }

    private fun executeSearch(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        val state = initializeState(origin)
        val previousWarehouseOf = exploreBreadthFirst(destination, state) ?: return null
        visitedWarehouseCount = state.visitedWarehouseIds.size
        return reconstructPath(destination, previousWarehouseOf)
    }

    private fun initializeState(origin: Warehouse): BfsState {
        val state = BfsState()
        state.queue.addLast(origin)
        state.visitedWarehouseIds.add(origin.id)
        return state
    }

    private fun exploreBreadthFirst(destination: Warehouse, state: BfsState): Map<String, Warehouse>? {
        while (state.queue.isNotEmpty()) {
            val current = state.queue.removeFirst()

            if (hasReachedDestination(current, destination)) {
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
        val isFirstVisit = state.visitedWarehouseIds.add(neighbor.id)
        if (!isFirstVisit) return

        state.previousWarehouseOf[neighbor.id] = current
        state.queue.addLast(neighbor)
    }

    private fun hasReachedDestination(current: Warehouse, destination: Warehouse): Boolean =
        current.id == destination.id
}
