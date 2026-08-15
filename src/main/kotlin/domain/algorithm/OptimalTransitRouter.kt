package domain.algorithm

import domain.model.Route
import domain.model.Warehouse

class OptimalTransitRouter {

    fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>? {
        val state = DijkstraState(origin)
        while (state.unvisitedWarehouses.isNotEmpty()) {
            val current = extractLowestDistanceNode(state) ?: break
            if (hasReachedDestination(current, destination))
                return reconstructPath(destination, state.previousWarehouseOf)
            markAsVisited(current, state)
            updateNeighborDistances(current, state)
        }
        return null
    }

    private fun hasReachedDestination(current: Warehouse, destination: Warehouse): Boolean =
        current.id == destination.id

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

    private fun updateRouteDistance(route: Route, current: Warehouse, currentDist: Double, state: DijkstraState ) {
        val neighbor = route.destinationHub
        if (isVisited(neighbor, state)) return
        val newDist = currentDist + route.distanceKm
        if (isShorterPath(newDist, state.distances[neighbor.id])) {
            updateNeighborState(neighbor, current, newDist, state)
        }
    }

    private fun isVisited(warehouse: Warehouse, state: DijkstraState): Boolean =
        warehouse.id in state.visitedWarehouseIds

    private fun isShorterPath(newDist: Double, currentDist: Double?): Boolean =
        newDist < (currentDist ?: Double.MAX_VALUE)

    private fun updateNeighborState(neighbor: Warehouse, current: Warehouse, newDist: Double, state: DijkstraState) {
        state.distances[neighbor.id] = newDist
        state.previousWarehouseOf[neighbor.id] = current
        state.unvisitedWarehouses.add(neighbor)
    }

    private fun extractLowestDistanceNode(state: DijkstraState): Warehouse? {
        var lowestWarehouse: Warehouse? = null
        var lowestDistance = Double.MAX_VALUE
        state.unvisitedWarehouses.forEach { warehouse ->
            val dist = state.distances[warehouse.id] ?: Double.MAX_VALUE
            if (isSmallerDistance(dist, lowestDistance)) {
                lowestDistance = dist
                lowestWarehouse = warehouse
            }
        }
        return lowestWarehouse
    }

    private fun isSmallerDistance(dist: Double, lowestDistance: Double): Boolean =
        dist < lowestDistance

    private fun reconstructPath(destination: Warehouse, previousWarehouseOf: Map<String, Warehouse>): List<Warehouse>? {
        val path = mutableListOf<Warehouse>()
        var current: Warehouse? = destination
        while (current != null) {
            path.add(current)
            current = previousWarehouseOf[current.id]
        }
        return path.reversed()
    }
}
