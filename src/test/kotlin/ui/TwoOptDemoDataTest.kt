package ui

import ui.demo.DefaultTwoOptDemoData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TwoOptDemoDataTest {

    private val demoData = DefaultTwoOptDemoData()

    @Test
    fun `createTour should contain fifteen delivery stops and return to origin`() {
        val tour = demoData.createTour()

        assertEquals(EXPECTED_TOUR_SIZE, tour.size)
        assertEquals(tour.first().id, tour.last().id)
        assertEquals(
            EXPECTED_DELIVERY_STOP_COUNT,
            tour.drop(1).dropLast(1).size
        )
    }

    @Test
    fun `createTour should contain unique delivery stops`() {
        val tour = demoData.createTour()
        val deliveryStops = tour.drop(1).dropLast(1)

        assertEquals(
            deliveryStops.size,
            deliveryStops.map { it.id }.toSet().size
        )

        assertTrue(deliveryStops.none { it.id == tour.first().id })
    }

    companion object {
        private const val EXPECTED_DELIVERY_STOP_COUNT = 15
        private const val EXPECTED_TOUR_SIZE = 17
    }
}
