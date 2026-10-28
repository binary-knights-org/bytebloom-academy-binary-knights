package ui.demo

import domain.algorithm.smoothing.MovingAverageSmoother
import domain.model.telemetry.GpsCoordinate
import domain.service.GpsJitterStreamService
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.onEach
import java.util.Locale

private const val WINDOW_SIZE = 5
private const val SAMPLE_COUNT = 20
private const val DEMO_INTERVAL_MS = 100L
private const val PERCENTAGE_MULTIPLIER_ = 100.0
private const val NO_JUMP = "          -          "

internal suspend fun runGpsJitterSmoothingDemo() {
    val rawPoints = mutableListOf<GpsCoordinate>()
    val smoothedPoints = mutableListOf<GpsCoordinate>()

    printHeader()

    val noisyStream = GpsJitterStreamService()
        .streamNoisyCoordinates(
            intervalMs = DEMO_INTERVAL_MS,
            emissionCount = SAMPLE_COUNT
        )
        .onEach { rawPoints.add(it) }

    MovingAverageSmoother(WINDOW_SIZE)(noisyStream)
        .collectIndexed { index, smoothed ->
            val raw = rawPoints[index]
            val jump = if (index == 0) {
                NO_JUMP
            } else {
                formatJump(
                    rawJump = raw.distanceMetersTo(rawPoints[index - 1]),
                    smoothedJump = smoothed.distanceMetersTo(smoothedPoints.last())
                )
            }
            smoothedPoints.add(smoothed)
            printRow(index + 1, raw, smoothed, jump)
        }

    printSummary(rawPoints, smoothedPoints)
}

private fun printHeader() {
    println("\n============================================================")
    println("   GPS TELEMETRY JITTER SMOOTHER (window = $WINDOW_SIZE)")
    println("============================================================")
    println(" #  | RAW (lat, lon)         | SMOOTHED (lat, lon)    | JUMP raw -> smoothed | PHASE")
}

private fun printRow(
    number: Int,
    raw: GpsCoordinate,
    smoothed: GpsCoordinate,
    jump: String
) {
    val phase = if (number < WINDOW_SIZE) "warm-up ($number/$WINDOW_SIZE)" else "full window"
    println(
        "%02d | %.6f, %.6f | %.6f, %.6f | %s | %s".format(
            Locale.US,
            number,
            raw.latitude, raw.longitude,
            smoothed.latitude, smoothed.longitude,
            jump,
            phase
        )
    )
}

private fun formatJump(rawJump: Double, smoothedJump: Double): String =
    "%6.1f m -> %6.1f m".format(Locale.US, rawJump, smoothedJump)

private fun printSummary(raw: List<GpsCoordinate>, smoothed: List<GpsCoordinate>) {
    val rawLength = pathLengthMeters(raw)
    val smoothedLength = pathLengthMeters(smoothed)
    val reduction = if (rawLength > 0.0) {
        (rawLength - smoothedLength) / rawLength * PERCENTAGE_MULTIPLIER_
    } else {
        0.0
    }
    val steps = (raw.size - 1).coerceAtLeast(1)

    println("\n--- SUMMARY ---")
    println("Raw path length:      %.1f m (avg jump %.1f m)".format(Locale.US, rawLength, rawLength / steps))
    println("Smoothed path length: %.1f m (avg jump %.1f m)".format(Locale.US, smoothedLength, smoothedLength / steps))
    println("Erratic movement reduced by: %.1f%%".format(Locale.US, reduction))
    println("Note: a moving average trades jitter for a small lag behind the real position.")
}

private fun pathLengthMeters(points: List<GpsCoordinate>): Double =
    points.zipWithNext { from, to -> from.distanceMetersTo(to) }.sum()
