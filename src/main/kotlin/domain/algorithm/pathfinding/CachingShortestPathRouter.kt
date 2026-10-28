package domain.algorithm.pathfinding

import domain.model.Warehouse

class CachingShortestPathRouter(
    private val dijkstraRouter: ShortestPathRouter,
    private val cache: LruRouteCache
) : ShortestPathRouter {

    var cacheHits: Int = 0
    var cacheMisses: Int = 0
    var dijkstraCalls: Int = 0

    override suspend fun findShortestPath(
        origin: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {
        val key = RouteKey(
            originId = origin.id,
            destinationId = destination.id
        )

        val cachedPath = cache.getRoute(key)

        if (cachedPath != null) {
            cacheHits++
            return cachedPath
        }

        cacheMisses++
        dijkstraCalls++

        val path = dijkstraRouter.findShortestPath(origin, destination)

        if (path != null) {
            cache.storeRoute(key, path)
        }

        return path
    }
}
