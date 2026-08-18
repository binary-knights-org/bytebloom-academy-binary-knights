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
                ?: expandOneStep(backwardState, forwardState)
        }
        return intersection?.let { reconstructBidirectionalPath(it, forwardState, backwardState) }
    }

    private fun createInitialState(startWarehouse: Warehouse): BfsState {
        return BfsState().apply {
            queue.addLast(startWarehouse)
            visitedWarehouseIds.add(startWarehouse.id)
        }
    }

    private fun expandOneStep(
        currentState: BfsState,
        oppositeState: BfsState
    ): Warehouse? {
        if (currentState.queue.isEmpty()) return null
        val current = currentState.queue.removeFirst()
        return expandNeighbors(current, currentState, oppositeState)
    }

    private fun expandNeighbors(
        current: Warehouse,
        currentState: BfsState,
        oppositeState: BfsState
    ): Warehouse? {
        var intersection: Warehouse? = null
        for (route in current.outgoingRoutes) {
            intersection = processNeighbor(route.destinationHub, current, currentState, oppositeState)
            if (intersection != null) break
        }
        return intersection
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

    private fun reconstructBidirectionalPath(
        intersection: Warehouse,
        forwardState: BfsState,
        backwardState: BfsState
    ): List<Warehouse> {
        val forwardPath = tracePath(intersection, forwardState.previousWarehouseOf).reversed()
        val backwardStart = backwardState.previousWarehouseOf[intersection.id]
        val backwardPath = tracePath(backwardStart, backwardState.previousWarehouseOf)

        return forwardPath + backwardPath
    }

    private fun tracePath(
        start: Warehouse?,
        previousMap: Map<String, Warehouse>
    ): List<Warehouse> {
        val path = mutableListOf<Warehouse>()
        var current = start
        while (current != null) {
            path.add(current)
            current = previousMap[current.id]
        }
        return path
    }
}