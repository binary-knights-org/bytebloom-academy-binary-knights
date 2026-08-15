package domain.algorithm

import domain.model.Route
import domain.model.Warehouse

/**
 * ==============================================================================
 * SUB-TASK 4: OPTIMAL TRANSIT ROUTER (MANUAL DIJKSTRA'S ALGORITHM)
 * ==============================================================================
 *
 * 1. THEORETICAL COMPARISON: DIJKSTRA VS. BFS ROUTER
 * ------------------------------------------------------------------------------
 * - BFS Router:
 *   * Core Metric : Counts total transfers/hops (Number of Edges).
 *   * Edge Weights: Assumes uniform edge distance for all routes (Weight = 1).
 *   * Queue System: Standard First-In, First-Out (FIFO) Queue.
 *
 * - Manual Dijkstra Router:
 *   * Core Metric : Calculates cumulative physical distance (distanceKm).
 *   * Edge Weights: Accurately evaluates varying route distances between hubs.
 *   * Node Lookup : Custom manual lookup (`extractLowestDistanceNode`) without
 *                   built-in priority sorting libraries.
 *
 * 2. WHY BFS FAILS ON VARYING PHYSICAL DISTANCES
 * ------------------------------------------------------------------------------
 * BFS explores graphs level-by-level based purely on hop counts, assuming that a
 * route with fewer transfers is always shorter.
 *
 * In real transit networks where route physical distances vary:
 * - A direct 1-hop route might cover a physical distance of 100 km.
 * - A 2-hop route via an intermediate hub might sum up to only 5 km (2 km + 3 km).
 *
 * Because BFS marks target nodes as visited upon their first discovery in Level 1,
 * it prematurely terminates and returns the 100 km path, failing to discover
 * the mathematically shorter 2-hop path.
 *
 * 3. COMPARISON DIAGRAM
 * ------------------------------------------------------------------------------
 *                (100 km - 1 Hop)
 *          [S] -------------------> [T]  <-- BFS Choice (1 Hop = 100 km) ❌
 *           |                        ^
 *         (2 km)                  (3 km)
 *           v                        |
 *          [A] ----------------------+   <-- Dijkstra Choice (2 Hops = 5 km)
 *                        (5 km total)
 * ==============================================================================
 */


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
