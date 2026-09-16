package domain.algorithm.pathfinding

import domain.model.Warehouse

interface ShortestPathRouter {
    suspend fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>?
}
