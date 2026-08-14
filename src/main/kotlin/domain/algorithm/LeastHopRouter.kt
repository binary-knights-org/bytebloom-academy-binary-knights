package domain.algorithm

import domain.model.Warehouse

class LeastHopRouter {

    fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        if (origin.id == destination.id) return listOf(origin)
        return executeSearch(origin, destination)
    }

    private fun executeSearch(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        val state = initializeState(origin)
        val previousWarehouseOf = exploreBreadthFirst(destination, state) ?: return null
        return reconstructPath(origin, destination, previousWarehouseOf)
    }

    private fun initializeState(origin: Warehouse): BfsState {
        val state = BfsState()
        state.queue.addLast(origin)
        state.visitedWarehouseIds.add(origin.id)
        return state
    }

    private fun exploreBreadthFirst(destination: Warehouse, state: BfsState): Map<String, Warehouse>? {
        while (state.queue.isNotEmpty()) {
            val currentWarehouse = state.queue.removeFirst()
            if (currentWarehouse.id == destination.id) {
                return state.previousWarehouseOf
            }
            enqueueUnvisitedNeighbors(currentWarehouse, state)
        }

        return null
    }

    private fun enqueueUnvisitedNeighbors(currentWarehouse: Warehouse, state: BfsState) {
        for (route in currentWarehouse.outgoingRoutes) {
            enqueueNeighborIfUnvisited(route.destinationHub, currentWarehouse, state)
        }
    }

    private fun enqueueNeighborIfUnvisited(
        neighborWarehouse: Warehouse,
        currentWarehouse: Warehouse,
        state: BfsState
    ) {
        val isFirstVisit = state.visitedWarehouseIds.add(neighborWarehouse.id)
        if (!isFirstVisit) return

        state.previousWarehouseOf[neighborWarehouse.id] = currentWarehouse
        state.queue.addLast(neighborWarehouse)
    }

    private fun reconstructPath(
        origin: Warehouse,
        destination: Warehouse,
        previousWarehouseOf: Map<String, Warehouse>
    ): List<Warehouse> {
        val pathFromDestinationToOrigin = mutableListOf<Warehouse>()
        var currentWarehouse: Warehouse? = destination

        while (currentWarehouse != null) {
            pathFromDestinationToOrigin.add(currentWarehouse)
            if (currentWarehouse.id == origin.id) break
            currentWarehouse = previousWarehouseOf[currentWarehouse.id]
        }

        return pathFromDestinationToOrigin.reversed()
    }
}
