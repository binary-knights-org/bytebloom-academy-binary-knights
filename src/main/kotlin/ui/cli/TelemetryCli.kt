package ui.cli

import domain.model.telemetry.FleetTrackingCriteria
import domain.usecase.telemetry.TrackActiveFleetUseCase
import kotlinx.coroutines.flow.take

private const val MAX_TELEMETRY_NOTIFICATIONS = 10

internal class TelemetryCli(
    private val trackActiveFleetUseCase: TrackActiveFleetUseCase,
) {
    suspend fun run() {
        println("\u001B[36mTelemetry streaming started.")
        println(
            "\u001B[95mDisplaying up to " +
                "$MAX_TELEMETRY_NOTIFICATIONS delay notifications..."
        )

        var notificationCount = 0

        withLoadingIndicator("Waiting for telemetry") { stopLoading ->
            trackActiveFleetUseCase(criteria = FleetTrackingCriteria())
                .take(MAX_TELEMETRY_NOTIFICATIONS)
                .collect { notification ->
                    stopLoading()
                    notificationCount++

                    println(
                        "[Telemetry] " +
                            "Vehicle: ${notification.vehicleId} | " +
                            "Delay: ${notification.delayMinutes} min | " +
                            "Speed: ${notification.currentSpeedKmh} km/h | " +
                            "Fuel: ${notification.fuelPercentage}% | " +
                            "Reason: ${notification.reason}"
                    )
                }
        }

        println(
            "\u001B[95mTelemetry display completed. " +
                "Notifications: $notificationCount"
        )
    }
}
