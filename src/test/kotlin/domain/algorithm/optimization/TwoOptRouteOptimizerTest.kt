package domain.algorithm.optimization

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidDeliveryTourException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TwoOptRouteOptimizerTest {

    private val optimizer = TwoOptRouteOptimizer()
    private val distanceCalculator = GeographicDistanceCalculator()

    @Test
    fun `optimize should reject a tour that does not return to its origin`() {
        val tour = listOf(
            createWarehouse("A", 32.0, 35.0),
            createWarehouse("B", 32.1, 35.1),
            createWarehouse("C", 32.2, 35.2)
        )

        assertFailsWith<InvalidDeliveryTourException> {
            optimizer.optimize(tour)
        }
    }

    @Test
    fun `optimize should preserve the origin at both ends`() {
        val origin = createWarehouse("Origin", 32.0, 35.0)
        val firstStop = createWarehouse("A", 32.1, 35.1)
        val secondStop = createWarehouse("B", 32.2, 35.2)

        val tour = listOf(origin, firstStop, secondStop, origin)

        val optimizedTour = optimizer.optimize(tour)

        assertEquals(origin.id, optimizedTour.first().id)
        assertEquals(origin.id, optimizedTour.last().id)
    }

    @Test
    fun `optimize should not increase the total tour distance`() {
        val origin = createWarehouse("Origin", 32.0, 35.0)
        val firstStop = createWarehouse("A", 32.1, 35.1)
        val secondStop = createWarehouse("B", 32.2, 35.2)
        val thirdStop = createWarehouse("C", 32.1, 35.3)

        val tour = listOf(
            origin,
            firstStop,
            secondStop,
            thirdStop,
            origin
        )

        val optimizedTour = optimizer.optimize(tour)

        assertTrue(
            calculateTourDistance(optimizedTour) <=
                    calculateTourDistance(tour) + DISTANCE_TOLERANCE_KM
        )
    }

    @Test
    fun `optimize should preserve all delivery stops`() {
        val origin = createWarehouse("Origin", 32.0, 35.0)
        val firstStop = createWarehouse("A", 32.1, 35.1)
        val secondStop = createWarehouse("B", 32.2, 35.2)
        val thirdStop = createWarehouse("C", 32.1, 35.3)

        val tour = listOf(
            origin,
            firstStop,
            secondStop,
            thirdStop,
            origin
        )

        val optimizedTour = optimizer.optimize(tour)

        assertEquals(
            tour.map { it.id }.toSet(),
            optimizedTour.map { it.id }.toSet()
        )
        assertEquals(tour.size, optimizedTour.size)
    }


    @Test
    fun `optimize should reduce distance when a better tour exists`() {
        val origin = createWarehouse("Origin", 0.0, 0.0)
        val firstStop = createWarehouse("A", 0.0, 2.0)
        val secondStop = createWarehouse("B", 1.0, 1.0)
        val thirdStop = createWarehouse("C", 1.0, 2.0)

        val tour = listOf(
            origin,
            firstStop,
            secondStop,
            thirdStop,
            origin
        )

        val originalDistance = calculateTourDistance(tour)
        val optimizedTour = optimizer.optimize(tour)
        val optimizedDistance = calculateTourDistance(optimizedTour)

        assertTrue(
            optimizedDistance < originalDistance,
            "Expected 2-Opt to reduce the tour distance."
        )

        assertEquals(origin.id, optimizedTour.first().id)
        assertEquals(origin.id, optimizedTour.last().id)
    }


    private fun calculateTourDistance(
        tour: List<Warehouse>
    ): Double = tour.zipWithNext().sumOf { (origin, destination) ->
        distanceCalculator.distanceBetween(origin, destination)
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
