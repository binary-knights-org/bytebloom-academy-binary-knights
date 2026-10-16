package ui

import domain.usecase.telemetry.TrackActiveFleetUseCase
import kotlinx.coroutines.flow.take

private const val DEMO_ALERT_LIMIT = 100

internal suspend fun runTelemetryStreamingDemo(
    trackActiveFleetUseCase: TrackActiveFleetUseCase
) {
    println("\n============================================================")
    println("  SUB-TASK 2: REAL-TIME TELEMETRY STREAMING & FLEET TRACKING ")
    println("============================================================")

    println("\nReactive Delay Notifications (TrackActiveFleetUseCase):")
    trackActiveFleetUseCase()
        .take(DEMO_ALERT_LIMIT)
        .collect { alert ->
            println(
                "[DELAY ALERT] Vehicle: %-8s | Estimated Delay: %2d min | Reason: %s"
                    .format(
                        alert.vehicleId,
                        alert.delayMinutes,
                        alert.reason
                    )
            )
        }
}
