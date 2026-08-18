package domain.algorithm.pathfinding

import domain.model.Warehouse

interface ShortestPathRouter {
    fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>?
}