package domain.algorithm.optimization

import domain.model.RegionalZone
import domain.model.Warehouse
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteDistanceCalculatorTest {

    private val calculator = RouteDistanceCalculator()

    @Test
    fun `calculateTotalDistanceKm should return zero for a single warehouse`() {
        val warehouse = createWarehouse("Origin", 32.0, 35.0)

        val distance = calculator.calculateTotalDistanceKm(listOf(warehouse))

        assertEquals(0.0, distance, DISTANCE_TOLERANCE_KM)
    }

    @Test
    fun `calculateTotalDistanceKm should sum consecutive distances`() {
        val origin = createWarehouse("Origin", 32.0, 35.0)
        val firstStop = createWarehouse("A", 32.1, 35.0)
        val destination = createWarehouse("B", 32.2, 35.0)

        val distance = calculator.calculateTotalDistanceKm(
            listOf(origin, firstStop, destination)
        )

        val expectedDistance =
            GeographicDistanceCalculator().distanceBetween(origin, firstStop) +
                    GeographicDistanceCalculator().distanceBetween(firstStop, destination)

        assertEquals(expectedDistance, distance, DISTANCE_TOLERANCE_KM)
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
