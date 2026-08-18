package domain.algorithm

import domain.model.Warehouse

interface ShortestPathRouter {
    fun findShortestPath(origin: Warehouse, destination: Warehouse): List<Warehouse>?
}