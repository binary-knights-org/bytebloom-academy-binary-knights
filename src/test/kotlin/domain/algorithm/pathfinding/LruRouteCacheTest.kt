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
    fun `get returns null for an uncached route`() {
        val cache = LruRouteCache()
        val key = RouteKey("WH-001", "WH-002")

        assertNull(cache.get(key))
    }

    @Test
    fun `get returns the route stored by put`() {
        val cache = LruRouteCache()
        val key = RouteKey("WH-001", "WH-002")
        val path = listOf(
            createWarehouse("WH-001", "Origin"),
            createWarehouse("WH-002", "Destination")
        )

        cache.put(key, path)

        assertEquals(path, cache.get(key))
    }

    @Test
    fun `get moves accessed route to most recently used position`() {
        val cache = LruRouteCache(capacity = 2)
        val firstKey = RouteKey("WH-001", "WH-002")
        val secondKey = RouteKey("WH-003", "WH-004")
        val thirdKey = RouteKey("WH-005", "WH-006")

        val firstPath = listOf(createWarehouse("WH-001", "First"))
        val secondPath = listOf(createWarehouse("WH-003", "Second"))
        val thirdPath = listOf(createWarehouse("WH-005", "Third"))

        cache.put(firstKey, firstPath)
        cache.put(secondKey, secondPath)
        cache.get(firstKey)
        cache.put(thirdKey, thirdPath)

        assertEquals(
            listOf(firstPath, null, thirdPath),
            listOf(
                cache.get(firstKey),
                cache.get(secondKey),
                cache.get(thirdKey)
            )
        )
    }

    @Test
    fun `put evicts the least recently used route when capacity is exceeded`() {
        val cache = LruRouteCache(capacity = 2)
        val firstKey = RouteKey("WH-001", "WH-002")
        val secondKey = RouteKey("WH-003", "WH-004")
        val thirdKey = RouteKey("WH-005", "WH-006")

        cache.put(firstKey, listOf(createWarehouse("WH-001", "First")))
        cache.put(secondKey, listOf(createWarehouse("WH-003", "Second")))
        cache.put(thirdKey, listOf(createWarehouse("WH-005", "Third")))

        assertEquals(
            listOf(null, "Second", "Third"),
            listOf(
                cache.get(firstKey)?.first()?.name,
                cache.get(secondKey)?.first()?.name,
                cache.get(thirdKey)?.first()?.name
            )
        )
    }

    @Test
    fun `put updates the path of an existing route`() {
        val cache = LruRouteCache(capacity = 2)
        val key = RouteKey("WH-001", "WH-002")
        val updatedPath = listOf(createWarehouse("WH-001", "Updated"))

        cache.put(key, listOf(createWarehouse("WH-001", "Old")))
        cache.put(key, updatedPath)

        assertEquals(updatedPath, cache.get(key))
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
            cache.put(key, listOf(warehouse))
        }

        cache.put(
            keys.last(),
            listOf(createWarehouse("WH-051", "Warehouse 51"))
        )

        assertEquals(
            listOf(null, "Warehouse 2", "Warehouse 51"),
            listOf(
                cache.get(keys.first())?.first()?.name,
                cache.get(keys[1])?.first()?.name,
                cache.get(keys.last())?.first()?.name
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

        cache.put(forwardKey, forwardPath)
        cache.put(reverseKey, reversePath)

        assertEquals(
            listOf(forwardPath, reversePath),
            listOf(cache.get(forwardKey), cache.get(reverseKey))
        )
    }
}
