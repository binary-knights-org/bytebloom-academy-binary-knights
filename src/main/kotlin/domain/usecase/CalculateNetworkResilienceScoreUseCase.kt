package domain.usecase

import domain.model.Warehouse

private const val MINIMUM_NETWORK_SIZE = 1
private const val PERFECT_RESILIENCE_SCORE = 100.0

class CalculateNetworkResilienceScoreUseCase {
    operator fun invoke(warehouses: List<Warehouse>): Double {
        if (warehouses.size <= MINIMUM_NETWORK_SIZE) {
            return PERFECT_RESILIENCE_SCORE
        }
        val warehouseMap = warehouses.associateBy { it.id }
        val survivableBreakdowns = warehouses.count { removedWarehouse ->
            isNetworkConnectedWithout(removedWarehouse, warehouses, warehouseMap)
        }
        return (survivableBreakdowns.toDouble() / warehouses.size) * PERFECT_RESILIENCE_SCORE
    }

    private fun isNetworkConnectedWithout(
        removedWarehouse: Warehouse, warehouses: List<Warehouse>, warehouseMap: Map<String, Warehouse>
    ): Boolean {
        val remainingWarehouses = warehouses.filter { it.id != removedWarehouse.id }
        val startWarehouse = remainingWarehouses.firstOrNull() ?: return true
        val visitedWarehouseIds = traverseNetwork(startWarehouse, removedWarehouse.id, warehouseMap)

        return remainingWarehouses.all { it.id in visitedWarehouseIds }
    }

    private fun traverseNetwork(
        startWarehouse: Warehouse,
        removedWarehouseId: String,
        warehouseMap: Map<String, Warehouse>
    ): Set<String> {
        val allWarehouses = warehouseMap.values.toList()
        tailrec fun visit(queue: List<Warehouse>, visited: Set<String>): Set<String> {
            val current = queue.firstOrNull() ?: return visited
            val remainingQueue = queue.drop(1)

            val outgoingDestinations = current.outgoingRoutes
                .map { it.destinationHub.id }
            val incomingOrigins = allWarehouses
                .filter { w -> w.outgoingRoutes.any { route -> route.destinationHub.id == current.id } }
                .map { it.id }
            val neighborIds = (outgoingDestinations + incomingOrigins).distinct()

            val nextWarehouses = neighborIds
                .mapNotNull { id -> warehouseMap[id] }
                .filter { it.id != removedWarehouseId && it.id !in visited }
            val updatedVisited = visited + nextWarehouses.map { it.id }
            return visit(remainingQueue + nextWarehouses, updatedVisited)
        }

        return visit(queue = listOf(startWarehouse), visited = setOf(startWarehouse.id))
    }
}
