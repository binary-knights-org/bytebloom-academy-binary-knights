package domain.algorithm.pathfinding

import domain.model.Warehouse
import domain.repository.WarehouseRepository

abstract class BaseShortestPathRouter(
    protected val warehouseRepository: WarehouseRepository
) : ShortestPathRouter {

    var visitedWarehouseCount: Int = 0
        protected set

    override suspend fun findShortestPath(
        origin: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {
        visitedWarehouseCount = 0

        val allWarehouses = warehouseRepository.getAll()
        val warehousesById = allWarehouses.associateBy { it.id }

        val actualOrigin = warehousesById[origin.id]
        val actualDestination = warehousesById[destination.id]

        return when {
            actualOrigin == null || actualDestination == null -> null
            actualOrigin.id == actualDestination.id -> {
                visitedWarehouseCount = 1
                listOf(actualOrigin)
            }
            else -> executeSearch(actualOrigin, actualDestination, allWarehouses)
        }
    }

    protected abstract fun executeSearch(
        origin: Warehouse,
        destination: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse>?

    protected fun reconstructPath(
        destination: Warehouse,
        previousWarehouseOf: Map<String, Warehouse>
    ): List<Warehouse> {
        val path = mutableListOf<Warehouse>()
        var current: Warehouse? = destination

        while (current != null) {
            path.add(current)
            current = previousWarehouseOf[current.id]
        }
        return path.reversed()
    }
}
