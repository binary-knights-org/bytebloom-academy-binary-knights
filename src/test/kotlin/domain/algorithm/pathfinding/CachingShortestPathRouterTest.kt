package domain.algorithm.pathfinding

import domain.model.RegionalZone
import domain.model.Warehouse
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CachingShortestPathRouterTest {

    private fun createWarehouse(
        id: String,
        name: String
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = RegionalZone.NORTH,
        latitude = 32.0,
        longitude = 35.0
    )

    @Test
    fun `repeated route request uses cache instead of calling original router`() =
        runTest {
            val origin = createWarehouse("WH-001", "Origin")
            val destination = createWarehouse("WH-002", "Destination")
            val expectedPath = listOf(origin, destination)

            var routerCallCount = 0

            val originalRouter = object : ShortestPathRouter {
                override suspend fun findShortestPath(
                    origin: Warehouse,
                    destination: Warehouse
                ): List<Warehouse>? {
                    routerCallCount++
                    return expectedPath
                }
            }

            val cachingRouter = CachingShortestPathRouter(
                dijkstraRouter = originalRouter,
                cache = LruRouteCache()
            )

            val firstResult = cachingRouter.findShortestPath(origin, destination)
            val secondResult = cachingRouter.findShortestPath(origin, destination)

            assertEquals(
                Pair(listOf(expectedPath, expectedPath), 1),
                Pair(listOf(firstResult, secondResult), routerCallCount)
            )
        }

    @Test
    fun `null route result is not cached`() = runTest {
        val origin = createWarehouse("WH-001", "Origin")
        val destination = createWarehouse("WH-002", "Destination")

        var routerCallCount = 0

        val originalRouter = object : ShortestPathRouter {
            override suspend fun findShortestPath(
                origin: Warehouse,
                destination: Warehouse
            ): List<Warehouse>? {
                routerCallCount++
                return null
            }
        }

        val cachingRouter = CachingShortestPathRouter(
            dijkstraRouter = originalRouter,
            cache = LruRouteCache()
        )

        cachingRouter.findShortestPath(origin, destination)
        cachingRouter.findShortestPath(origin, destination)

        assertEquals(2, routerCallCount)
    }
}