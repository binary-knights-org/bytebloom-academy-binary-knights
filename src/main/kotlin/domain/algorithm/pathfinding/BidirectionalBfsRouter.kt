package domain.algorithm.pathfinding

import domain.model.Warehouse
import domain.repository.WarehouseRepository

private const val SKIP_DUPLICATE_INTERSECTION_NODE_COUNT = 1

class BidirectionalBfsRouter(
    private val warehouseRepository: WarehouseRepository
) : ShortestPathRouter {

    var visitedWarehouseCount = 0
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

            else -> executeBidirectionalSearch(
                actualOrigin,
                actualDestination,
                allWarehouses
            )
        }

        return path
    }

    private fun executeBidirectionalSearch(
        origin: Warehouse,
        destination: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse>? {
        val forwardState = createInitialState(origin)
        val backwardState = createInitialState(destination)
        var intersection: Warehouse? = null

        while (forwardState.queue.isNotEmpty() && backwardState.queue.isNotEmpty() && intersection == null) {
            val expandForward = forwardState.queue.size <= backwardState.queue.size

            intersection =
                if (expandForward) expandOneStep(forwardState, backwardState, allWarehouses, false)
                else expandOneStep(backwardState, forwardState, allWarehouses, true)
        }
        return intersection?.let { buildUnifiedPath(it, forwardState, backwardState) }
    }

    private fun createInitialState(startWarehouse: Warehouse): BfsState {
        return BfsState().apply {
            queue.addLast(startWarehouse)
            visitedWarehouseIds.add(startWarehouse.id)
        }
    }

    private fun expandOneStep(
        currentState: BfsState,
        oppositeState: BfsState,
        allWarehouses: List<Warehouse>,
        searchBackward: Boolean
    ): Warehouse? {

        if (currentState.queue.isEmpty()) return null

        val currentWarehouse = currentState.queue.removeFirst()
        visitedWarehouseCount++

        val neighbors = if (searchBackward) findPredecessors(currentWarehouse, allWarehouses)
        else currentWarehouse.outgoingRoutes.map { it.destinationHub }

        var intersection: Warehouse? = null
        for (neighbor in neighbors) {
            intersection = processNeighbor(neighbor, currentWarehouse, currentState, oppositeState)
            if (intersection != null) break
        }
        return intersection
    }

    private fun findPredecessors(
        warehouse: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse> {
        return allWarehouses.filter { candidate ->
            candidate.outgoingRoutes.any { route ->
                route.destinationHub.id == warehouse.id
            }
        }
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

    private fun buildUnifiedPath(
        intersection: Warehouse,
        forwardState: BfsState,
        backwardState: BfsState
    ): List<Warehouse> {

        val forwardPath = reconstructPath(intersection, forwardState.previousWarehouseOf)
        val backwardPath = reconstructBackwardPath(intersection, backwardState.previousWarehouseOf)
        return forwardPath + backwardPath.drop(SKIP_DUPLICATE_INTERSECTION_NODE_COUNT)
    }

    private fun reconstructBackwardPath(
        intersection: Warehouse,
        previousWarehouseOf: Map<String, Warehouse>
    ): List<Warehouse> {

        val path = mutableListOf<Warehouse>()
        var current: Warehouse? = intersection

        while (current != null) {
            path.add(current)
            current = previousWarehouseOf[current.id]
        }

        return path
    }
}
