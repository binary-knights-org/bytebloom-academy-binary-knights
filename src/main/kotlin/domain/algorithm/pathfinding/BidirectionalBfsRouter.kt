package domain.algorithm.pathfinding

import domain.model.Warehouse

class BidirectionalBfsRouter : ShortestPathRouter {

    override fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        if (origin.id == destination.id) return listOf(origin) // return 1
        val forwardState = createInitialState(origin)
        val backwardState = createInitialState(destination)
        var intersection: Warehouse? = null
        while (forwardState.queue.isNotEmpty() && backwardState.queue.isNotEmpty() && intersection == null) {
            intersection = expandOneStep(forwardState, backwardState)
                ?: expandOneStep(backwardState, forwardState)
        }
        return intersection?.let { reconstructBidirectionalPath(it, forwardState, backwardState) } // return 2
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
        var intersection: Warehouse? = null
        for (route in current.outgoingRoutes) {
            val neighbor = route.destinationHub
            if (!currentState.visitedWarehouseIds.add(neighbor.id)) continue
            currentState.previousWarehouseOf[neighbor.id] = current
            if (neighbor.id in oppositeState.visitedWarehouseIds) {
                intersection = neighbor
                break
            }
            currentState.queue.addLast(neighbor)
        }
        return intersection
    }

    private fun reconstructBidirectionalPath(
        intersection: Warehouse,
        forwardState: BfsState,
        backwardState: BfsState
    ): List<Warehouse> {
        val pathFromOrigin = mutableListOf<Warehouse>()
        var currForward: Warehouse? = intersection
        while (currForward != null) {
            pathFromOrigin.add(currForward)
            currForward = forwardState.previousWarehouseOf[currForward.id]
        }
        val forwardPath = pathFromOrigin.reversed()
        val pathToDestination = mutableListOf<Warehouse>()
        var currBackward = backwardState.previousWarehouseOf[intersection.id]
        while (currBackward != null) {
            pathToDestination.add(currBackward)
            currBackward = backwardState.previousWarehouseOf[currBackward.id]
        }
        return forwardPath + pathToDestination
    }
}

