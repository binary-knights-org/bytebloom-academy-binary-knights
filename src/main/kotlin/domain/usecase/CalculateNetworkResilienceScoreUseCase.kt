package domain.usecase

import domain.model.Warehouse

private const val MINIMUM_NETWORK_SIZE = 1
private const val PERFECT_RESILIENCE_SCORE = 100.0

data class NetworkGraphContext(
    val warehouses: List<Warehouse>,
    val warehouseMap: Map<String, Warehouse>,
    val incomingOriginsByDestinationId: Map<String, List<String>>
)

class CalculateNetworkResilienceScoreUseCase {
    operator fun invoke(warehouses: List<Warehouse>): Double {
        if (warehouses.size <= MINIMUM_NETWORK_SIZE) {
            return PERFECT_RESILIENCE_SCORE
        }

        val warehouseMap = warehouses.associateBy { it.id }
        val incomingOriginsByDestinationId = buildIncomingIndex(warehouses)

        val context = NetworkGraphContext(
            warehouses = warehouses,
            warehouseMap = warehouseMap,
            incomingOriginsByDestinationId = incomingOriginsByDestinationId
        )

        val survivableBreakdowns = warehouses.count { removedWarehouse ->
            isNetworkConnectedWithout(
                removedWarehouse, context
            )
        }
        return (survivableBreakdowns.toDouble() / warehouses.size) * PERFECT_RESILIENCE_SCORE
    }

    private fun buildIncomingIndex(warehouses: List<Warehouse>): Map<String, List<String>> {
        return warehouses.flatMap { origin -> origin.outgoingRoutes.map { it.destinationHub.id to origin.id } }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
    }

    private fun isNetworkConnectedWithout(
        removedWarehouse: Warehouse, context: NetworkGraphContext
    ): Boolean {
        val remainingWarehouses = context.warehouses.filter { it.id != removedWarehouse.id }
        val startWarehouse = remainingWarehouses.firstOrNull() ?: return true

        val visitedWarehouseIds = traverseNetwork(
            startWarehouse, removedWarehouse.id, context
        )

        return remainingWarehouses.all { it.id in visitedWarehouseIds }
    }

    private fun traverseNetwork(
        startWarehouse: Warehouse, removedWarehouseId: String, context: NetworkGraphContext
    ): Set<String> {
        tailrec fun visit(queue: List<Warehouse>, visited: Set<String>): Set<String> {
            val current = queue.firstOrNull() ?: return visited
            val remainingQueue = queue.drop(1)

            val outgoingDestinations = current.outgoingRoutes.map { it.destinationHub.id }
            val incomingOrigins = context.incomingOriginsByDestinationId[current.id].orEmpty()
            val neighborIds = (outgoingDestinations + incomingOrigins).distinct()

            val nextWarehouses = neighborIds.mapNotNull { id -> context.warehouseMap[id] }
                .filter { it.id != removedWarehouseId && it.id !in visited }

            val updatedVisited = visited + nextWarehouses.map { it.id }
            return visit(remainingQueue + nextWarehouses, updatedVisited)
        }

        return visit(queue = listOf(startWarehouse), visited = setOf(startWarehouse.id))
    }
}
