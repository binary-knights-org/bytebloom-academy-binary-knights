package domain.algorithm.smoothing

import domain.model.exception.InvalidWindowSizeException
import domain.model.telemetry.GpsCoordinate
import domain.service.GpsJitterStreamService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MovingAverageSmootherTest {

    private val tolerance = 1e-9

    @Test
    fun `should average available points during cold start without dropping any`() = runTest {
        // Given
        val input = listOf(point(10.0, 100.0), point(20.0, 110.0), point(30.0, 120.0))

        // When
        val result = MovingAverageSmoother(windowSize = 5)(input.asFlow()).toList()

        // Then
        assertEquals(3, result.size)
        assertPoint(point(10.0, 100.0), result[0])
        assertPoint(point(15.0, 105.0), result[1])
        assertPoint(point(20.0, 110.0), result[2])
    }

    @Test
    fun `should slide the window once it is full`() = runTest {
        // Given
        val input = (1..7).map { point(lat = it * 10.0, lon = 90.0 + it * 10.0) }

        // When
        val result = MovingAverageSmoother(windowSize = 3)(input.asFlow()).toList()

        // Then
        val expectedLatitudes = listOf(10.0, 15.0, 20.0, 30.0, 40.0, 50.0, 60.0)
        val expectedLongitudes = listOf(100.0, 105.0, 110.0, 120.0, 130.0, 140.0, 150.0)
        assertEquals(input.size, result.size)
        result.forEachIndexed { index, actual ->
            assertPoint(point(expectedLatitudes[index], expectedLongitudes[index]), actual)
        }
    }

    @Test
    fun `should return the same points when window size is one`() = runTest {
        // Given
        val input = listOf(point(10.0, 100.0), point(20.0, 110.0))

        // When
        val result = MovingAverageSmoother(windowSize = 1)(input.asFlow()).toList()

        // Then
        assertEquals(input, result)
    }

    @Test
    fun `should keep independent state for every collection`() = runTest {
        // Given
        val input = listOf(point(10.0, 100.0), point(20.0, 110.0), point(30.0, 120.0))
        val smoothed = MovingAverageSmoother(windowSize = 3)(input.asFlow())

        // When
        val first = smoothed.toList()
        val second = smoothed.toList()

        // Then
        assertEquals(first, second)
    }

    @Test
    fun `should reject a window size below one`() {
        assertFailsWith<InvalidWindowSizeException> { MovingAverageSmoother(windowSize = 0) }
        assertFailsWith<InvalidWindowSizeException> { MovingAverageSmoother(windowSize = -3) }
    }

    @Test
    fun `should reduce the erratic path length of a noisy stream`() = runTest {
        // Given
        val raw = GpsJitterStreamService(seed = 7)
            .streamNoisyCoordinates(intervalMs = 0, emissionCount = 50)
            .toList()

        // When
        val smoothed = MovingAverageSmoother(windowSize = 5)(raw.asFlow()).toList()

        // Then
        assertEquals(raw.size, smoothed.size)
        assertTrue(pathLengthMeters(smoothed) < pathLengthMeters(raw))
    }

    private fun point(lat: Double, lon: Double) = GpsCoordinate(lat, lon)

    private fun assertPoint(expected: GpsCoordinate, actual: GpsCoordinate) {
        assertEquals(expected.latitude, actual.latitude, tolerance)
        assertEquals(expected.longitude, actual.longitude, tolerance)
    }

    private fun pathLengthMeters(points: List<GpsCoordinate>): Double =
        points.zipWithNext { from, to -> from.distanceMetersTo(to) }.sum()
}
