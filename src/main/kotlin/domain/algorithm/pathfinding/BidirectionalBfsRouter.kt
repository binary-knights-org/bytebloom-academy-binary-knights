package domain.algorithm.pathfinding

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class BidirectionalBfsRouter(
    warehouseRepository: WarehouseRepository
) : BaseShortestPathRouter(warehouseRepository) {

    override fun executeSearch(
        origin: Warehouse,
        destination: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse>? {
        val forwardState = createInitialState(origin)
        val backwardState = createInitialState(destination)
        var intersection: Warehouse? = null

        while (forwardState.queue.isNotEmpty() && backwardState.queue.isNotEmpty() && intersection == null) {
            val expandForward = forwardState.queue.size <= backwardState.queue.size

            intersection = if (expandForward) {
                expandOneStep(forwardState, backwardState, allWarehouses, searchBackward = false)
            } else {
                expandOneStep(backwardState, forwardState, allWarehouses, searchBackward = true)
            }
        }

        return intersection?.let { buildUnifiedPath(it, forwardState, backwardState) }
    }

    private fun createInitialState(startWarehouse: Warehouse): BfsState =
        BfsState().apply {
            queue.addLast(startWarehouse)
            visitedWarehouseIds.add(startWarehouse.id)
        }

    private fun expandOneStep(
        currentState: BfsState,
        oppositeState: BfsState,
        allWarehouses: List<Warehouse>,
        searchBackward: Boolean
    ): Warehouse? {
        val currentWarehouse = currentState.queue.removeFirstOrNull() ?: return null
        visitedWarehouseCount++

        val neighbors = if (searchBackward) {
            findPredecessors(currentWarehouse, allWarehouses)
        } else {
            currentWarehouse.outgoingRoutes.map { it.destinationHub }
        }

        return neighbors.firstNotNullOfOrNull { neighbor ->
            processNeighbor(neighbor, currentWarehouse, currentState, oppositeState)
        }
    }

    private fun findPredecessors(
        warehouse: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse> = allWarehouses.filter { candidate ->
        candidate.outgoingRoutes.any { it.destinationHub.id == warehouse.id }
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
        return forwardPath + backwardPath.drop(SKIP_DUPLICATE_INTERSECTION)
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

    private companion object {
        const val SKIP_DUPLICATE_INTERSECTION = 1
    }
}
