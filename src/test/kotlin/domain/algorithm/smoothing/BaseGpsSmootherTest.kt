package domain.algorithm.smoothing

import domain.model.telemetry.GpsCoordinate
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseGpsSmootherTest {

    @Test
    fun `should call the hook once per reading with a sliding window`() = runTest {
        // Given
        val input = (1..5).map { GpsCoordinate(latitude = it * 10.0, longitude = it * 10.0) }
        val smoother = RecordingSmoother(windowSize = 3)

        // When
        val result = smoother(input.asFlow()).toList()

        // Then
        assertEquals(listOf(1, 2, 3, 3, 3), smoother.seenWindowSizes)
        assertEquals(input, result)
    }

    private class RecordingSmoother(windowSize: Int) : BaseGpsSmoother(windowSize) {
        val seenWindowSizes = mutableListOf<Int>()

        override fun calculateSmoothedCoordinate(window: List<GpsCoordinate>): GpsCoordinate {
            seenWindowSizes.add(window.size)
            return window.last()
        }
    }
}
