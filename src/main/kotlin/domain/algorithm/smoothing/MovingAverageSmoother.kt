package domain.algorithm.smoothing

import domain.model.telemetry.GpsCoordinate

class MovingAverageSmoother(windowSize: Int) : BaseGpsSmoother(windowSize) {

    override fun calculateSmoothedCoordinate(window: List<GpsCoordinate>): GpsCoordinate =
        GpsCoordinate(
            latitude = window.sumOf { it.latitude } / window.size,
            longitude = window.sumOf { it.longitude } / window.size
        )
}
