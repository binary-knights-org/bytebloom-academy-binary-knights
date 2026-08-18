package domain.algorithm.pathfinding

import domain.model.Warehouse

class BidirectionalBfsRouter : ShortestPathRouter {

    override fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        if (origin.id == destination.id) return listOf(origin)
        val forwardState = createInitialState(origin)
        val backwardState = createInitialState(destination)
        var intersection: Warehouse? = null

        while (forwardState.queue.isNotEmpty() && backwardState.queue.isNotEmpty() && intersection == null) {
            intersection = expandOneStep(forwardState, backwardState)
            if (intersection == null) {
                intersection = expandOneStep(backwardState, forwardState)
            }
        }

        return null
    }

    private fun createInitialState(startWarehouse: Warehouse): BfsState {
        return BfsState().apply {
            queue.addLast(startWarehouse)
            visitedWarehouseIds.add(startWarehouse.id)
        }
    }

    private fun expandOneStep(currentState: BfsState, oppositeState: BfsState): Warehouse? {
        if (currentState.queue.isEmpty()) {
            return null
        }
        val currentWarehouse = currentState.queue.removeFirst()
        return expandNeighbors(currentWarehouse, currentState, oppositeState)
    }

    private fun expandNeighbors(
        currentWarehouse: Warehouse,
        currentState: BfsState,
        oppositeState: BfsState
    ): Warehouse? {
        for (route in currentWarehouse.outgoingRoutes) {
            val neighbor = route.destinationHub
            val intersection = processNeighbor(neighbor, currentWarehouse, currentState, oppositeState)
            if (intersection != null) {
                return intersection
            }
        }
        return null
    }

    private fun processNeighbor(
        neighbor: Warehouse,
        current: Warehouse,
        currentState: BfsState,
        oppositeState: BfsState
    ): Warehouse? {
        if (!currentState.visitedWarehouseIds.add(neighbor.id)) return null

        currentState.previousWarehouseOf[neighbor.id] = current
        val isIntersection = neighbor.id in oppositeState.visitedWarehouseIds

        if (!isIntersection) {
            currentState.queue.addLast(neighbor)
        }

        return if (isIntersection) neighbor else null
    }
}
