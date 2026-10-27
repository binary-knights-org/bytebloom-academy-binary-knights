package domain.service

import domain.model.telemetry.GpsCoordinate
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GpsJitterStreamServiceTest {

    private val service = GpsJitterStreamService(seed = 1)
    private val start = GpsJitterStreamService.DEFAULT_START

    @Test
    fun `should emit the requested number of coordinates`() = runTest {
        val result = service.streamNoisyCoordinates(intervalMs = 0, emissionCount = 12).toList()

        assertEquals(12, result.size)
    }

    @Test
    fun `should emit exact positions when jitter is zero`() = runTest {
        val result = service.streamNoisyCoordinates(
            maxJitterMeters = 0.0,
            intervalMs = 0,
            emissionCount = 5
        ).toList()

        result.forEachIndexed { step, actual ->
            assertEquals(service.truePositionAt(start, step), actual)
        }
    }

    @Test
    fun `should keep every reading within the jitter bound of the true position`() = runTest {
        val maxJitterMeters = 20.0
        val bound = maxJitterMeters * sqrt(2.0) * 1.001

        val result = service.streamNoisyCoordinates(
            maxJitterMeters = maxJitterMeters,
            intervalMs = 0,
            emissionCount = 100
        ).toList()

        result.forEachIndexed { step, actual ->
            val truth: GpsCoordinate = service.truePositionAt(start, step)
            assertTrue(actual.distanceMetersTo(truth) <= bound)
        }
    }

    @Test
    fun `should produce the same stream for the same seed`() = runTest {
        val first = GpsJitterStreamService(seed = 9)
            .streamNoisyCoordinates(intervalMs = 0, emissionCount = 10).toList()
        val second = GpsJitterStreamService(seed = 9)
            .streamNoisyCoordinates(intervalMs = 0, emissionCount = 10).toList()

        assertEquals(first, second)
    }
}
