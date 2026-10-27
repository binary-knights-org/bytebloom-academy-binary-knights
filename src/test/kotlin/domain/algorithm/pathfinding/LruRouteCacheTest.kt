package domain.algorithm.pathfinding

import domain.model.RegionalZone
import domain.model.Warehouse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LruRouteCacheTest {

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
    fun `getRoute returns null for an uncached route`() {
        val cache = LruRouteCache()
        val key = RouteKey("WH-001", "WH-002")

        assertNull(cache.getRoute(key))
    }

    @Test
    fun `getRoute returns the route stored by storeRoute`() {
        val cache = LruRouteCache()
        val key = RouteKey("WH-001", "WH-002")
        val path = listOf(
            createWarehouse("WH-001", "Origin"),
            createWarehouse("WH-002", "Destination")
        )

        cache.storeRoute(key, path)

        assertEquals(path, cache.getRoute(key))
    }

    @Test
    fun `getRoute moves accessed route to most recently used position`() {
        val cache = LruRouteCache(capacity = 2)
        val firstKey = RouteKey("WH-001", "WH-002")
        val secondKey = RouteKey("WH-003", "WH-004")
        val thirdKey = RouteKey("WH-005", "WH-006")

        val firstPath = listOf(createWarehouse("WH-001", "First"))
        val secondPath = listOf(createWarehouse("WH-003", "Second"))
        val thirdPath = listOf(createWarehouse("WH-005", "Third"))

        cache.storeRoute(firstKey, firstPath)
        cache.storeRoute(secondKey, secondPath)
        cache.getRoute(firstKey)
        cache.storeRoute(thirdKey, thirdPath)

        assertEquals(
            listOf(firstPath, null, thirdPath),
            listOf(
                cache.getRoute(firstKey),
                cache.getRoute(secondKey),
                cache.getRoute(thirdKey)
            )
        )
    }

    @Test
    fun `storeRoute evicts the least recently used route when capacity is exceeded`() {
        val cache = LruRouteCache(capacity = 2)
        val firstKey = RouteKey("WH-001", "WH-002")
        val secondKey = RouteKey("WH-003", "WH-004")
        val thirdKey = RouteKey("WH-005", "WH-006")

        cache.storeRoute(firstKey, listOf(createWarehouse("WH-001", "First")))
        cache.storeRoute(secondKey, listOf(createWarehouse("WH-003", "Second")))
        cache.storeRoute(thirdKey, listOf(createWarehouse("WH-005", "Third")))

        assertEquals(
            listOf(null, "Second", "Third"),
            listOf(
                cache.getRoute(firstKey)?.first()?.name,
                cache.getRoute(secondKey)?.first()?.name,
                cache.getRoute(thirdKey)?.first()?.name
            )
        )
    }

    @Test
    fun `storeRoute updates the path of an existing route`() {
        val cache = LruRouteCache(capacity = 2)
        val key = RouteKey("WH-001", "WH-002")
        val updatedPath = listOf(createWarehouse("WH-001", "Updated"))

        cache.storeRoute(key, listOf(createWarehouse("WH-001", "Old")))
        cache.storeRoute(key, updatedPath)

        assertEquals(updatedPath, cache.getRoute(key))
    }

    @Test
    fun `cache retains fifty routes and evicts the oldest route`() {
        val cache = LruRouteCache()
        val keys = (1..51).map { index ->
            RouteKey("WH-$index", "WH-${index + 100}")
        }

        keys.take(50).forEachIndexed { index, key ->
            val warehouse = createWarehouse(
                id = "WH-${index + 1}",
                name = "Warehouse ${index + 1}"
            )
            cache.storeRoute(key, listOf(warehouse))
        }

        cache.storeRoute(
            keys.last(),
            listOf(createWarehouse("WH-051", "Warehouse 51"))
        )

        assertEquals(
            listOf(null, "Warehouse 2", "Warehouse 51"),
            listOf(
                cache.getRoute(keys.first())?.first()?.name,
                cache.getRoute(keys[1])?.first()?.name,
                cache.getRoute(keys.last())?.first()?.name
            )
        )
    }

    @Test
    fun `constructor rejects zero capacity`() {
        assertFailsWith<IllegalArgumentException> {
            LruRouteCache(capacity = 0)
        }
    }

    @Test
    fun `constructor rejects negative capacity`() {
        assertFailsWith<IllegalArgumentException> {
            LruRouteCache(capacity = -1)
        }
    }

    @Test
    fun `cache treats forward and reverse routes as different entries`() {
        val cache = LruRouteCache()
        val forwardKey = RouteKey("WH-001", "WH-002")
        val reverseKey = RouteKey("WH-002", "WH-001")

        val forwardPath = listOf(
            createWarehouse("WH-001", "Origin"),
            createWarehouse("WH-002", "Destination")
        )
        val reversePath = listOf(
            createWarehouse("WH-002", "Destination"),
            createWarehouse("WH-001", "Origin")
        )

        cache.storeRoute(forwardKey, forwardPath)
        cache.storeRoute(reverseKey, reversePath)

        assertEquals(
            listOf(forwardPath, reversePath),
            listOf(cache.getRoute(forwardKey), cache.getRoute(reverseKey))
        )
    }
}
