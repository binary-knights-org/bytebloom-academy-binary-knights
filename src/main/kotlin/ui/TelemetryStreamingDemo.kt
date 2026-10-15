package ui

import domain.usecase.telemetry.TrackActiveFleetUseCase
import kotlinx.coroutines.flow.take

internal suspend fun runTelemetryStreamingDemo(
    trackActiveFleetUseCase: TrackActiveFleetUseCase
) {
    println("\n============================================================")
    println("  SUB-TASK 2: REAL-TIME TELEMETRY STREAMING & FLEET TRACKING ")
    println("============================================================")

    println("\nReactive Delay Notifications (TrackActiveFleetUseCase):")
    trackActiveFleetUseCase()
        .take(100)
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
