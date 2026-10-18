package domain.algorithm.pathfinding

import domain.model.Route
import domain.model.Warehouse
import domain.repository.WarehouseRepository

class OptimalTransitRouter(
    warehouseRepository: WarehouseRepository
) : BaseShortestPathRouter(warehouseRepository) {

    override fun executeSearch(
        origin: Warehouse,
        destination: Warehouse,
        allWarehouses: List<Warehouse>
    ): List<Warehouse>? {
        val state = DijkstraState(origin)

        while (state.unvisitedWarehouses.isNotEmpty()) {
            val current = extractLowestDistanceNode(state) ?: break

            if (current.id == destination.id) {
                visitedWarehouseCount = state.visitedWarehouseIds.size + 1
                return reconstructPath(destination, state.previousWarehouseOf)
            }

            markAsVisited(current, state)
            updateNeighborDistances(current, state)
        }
        return null
    }

    private fun markAsVisited(current: Warehouse, state: DijkstraState) {
        state.unvisitedWarehouses.remove(current)
        state.visitedWarehouseIds.add(current.id)
    }

    private fun updateNeighborDistances(current: Warehouse, state: DijkstraState) {
        val currentDist = state.distances[current.id] ?: Double.MAX_VALUE
        current.outgoingRoutes.forEach { route ->
            updateRouteDistance(route, current, currentDist, state)
        }
    }

    private fun updateRouteDistance(
        route: Route,
        current: Warehouse,
        currentDist: Double,
        state: DijkstraState
    ) {
        val neighbor = route.destinationHub
        if (neighbor.id in state.visitedWarehouseIds) return

        val newDist = currentDist + route.distanceKm
        val existingDist = state.distances[neighbor.id] ?: Double.MAX_VALUE

        if (newDist < existingDist) {
            state.distances[neighbor.id] = newDist
            state.previousWarehouseOf[neighbor.id] = current
            state.unvisitedWarehouses.add(neighbor)
        }
    }

    private fun extractLowestDistanceNode(state: DijkstraState): Warehouse? =
        state.unvisitedWarehouses.minByOrNull { state.distances[it.id] ?: Double.MAX_VALUE }
}
