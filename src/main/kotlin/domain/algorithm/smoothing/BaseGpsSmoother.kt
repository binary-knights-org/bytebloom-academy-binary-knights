package domain.algorithm.smoothing

import domain.model.exception.InvalidWindowSizeException
import domain.model.telemetry.GpsCoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


abstract class BaseGpsSmoother(private val windowSize: Int) {

    init {
        if (windowSize < MIN_WINDOW_SIZE) {
            throw InvalidWindowSizeException()
        }
    }

    operator fun invoke(readings: Flow<GpsCoordinate>): Flow<GpsCoordinate> = flow {
        val window = ArrayDeque<GpsCoordinate>(windowSize)

        readings.collect { reading ->
            slideWindow(window, reading)
            emit(calculateSmoothedCoordinate(window))
        }
    }


    protected abstract fun calculateSmoothedCoordinate(window: List<GpsCoordinate>): GpsCoordinate

    private fun slideWindow(window: ArrayDeque<GpsCoordinate>, reading: GpsCoordinate) {
        if (window.size == windowSize) {
            window.removeFirst()
        }
        window.addLast(reading)
    }

    private companion object {
        const val MIN_WINDOW_SIZE = 1
    }
}
