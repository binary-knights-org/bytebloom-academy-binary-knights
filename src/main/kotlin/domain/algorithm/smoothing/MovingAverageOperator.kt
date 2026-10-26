package domain.algorithm.smoothing


import domain.model.exception.InvalidWindowSizeException
import domain.model.telemetry.GpsCoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val MINIMUM_WINDOW_SIZE = 1


fun Flow<GpsCoordinate>.movingAverage(windowSize: Int): Flow<GpsCoordinate> {
    if (windowSize < MINIMUM_WINDOW_SIZE) {
    throw InvalidWindowSizeException()
    }

    val upstream = this

    return flow {
        val window = ArrayDeque<GpsCoordinate>(windowSize)

        upstream.collect { point ->
            if (window.size == windowSize) {
                window.removeFirst()
            }
            window.addLast(point)

            emit(
                GpsCoordinate(
                    latitude = window.sumOf { it.latitude } / window.size,
                    longitude = window.sumOf { it.longitude } / window.size
                )
            )
        }
    }
}
