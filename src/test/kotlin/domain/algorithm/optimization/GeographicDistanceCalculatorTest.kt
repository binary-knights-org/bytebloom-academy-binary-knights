package domain.algorithm.optimization

import domain.model.RegionalZone
import domain.model.Warehouse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeographicDistanceCalculatorTest {

    private val calculator = GeographicDistanceCalculator()

    @Test
    fun `distance between same coordinates should be zero`() {
        val warehouse = createWarehouse(
            name = "Origin",
            latitude = 32.2211,
            longitude = 35.2544
        )

        val distance = calculator.distanceBetween(warehouse, warehouse)

        assertEquals(0.0, distance, DISTANCE_TOLERANCE_KM)
    }

    @Test
    fun `distance should be symmetric`() {
        val origin = createWarehouse(
            name = "Origin",
            latitude = 32.2211,
            longitude = 35.2544
        )
        val destination = createWarehouse(
            name = "Destination",
            latitude = 31.9038,
            longitude = 35.2034
        )

        val forwardDistance =
            calculator.distanceBetween(origin, destination)

        val reverseDistance =
            calculator.distanceBetween(destination, origin)

        assertEquals(
            forwardDistance,
            reverseDistance,
            DISTANCE_TOLERANCE_KM
        )
    }

    @Test
    fun `distance between different coordinates should be positive`() {
        val origin = createWarehouse(
            name = "Origin",
            latitude = 32.2211,
            longitude = 35.2544
        )
        val destination = createWarehouse(
            name = "Destination",
            latitude = 31.9038,
            longitude = 35.2034
        )

        val distance = calculator.distanceBetween(origin, destination)

        assertTrue(distance > 0.0)
    }

    private fun createWarehouse(
        name: String,
        latitude: Double,
        longitude: Double
    ): Warehouse = Warehouse(
        name = name,
        regionalZone = RegionalZone.NORTH,
        latitude = latitude,
        longitude = longitude
    )

    companion object {
        private const val DISTANCE_TOLERANCE_KM = 0.000001
    }
}
