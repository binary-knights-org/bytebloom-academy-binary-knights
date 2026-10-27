package ui.cli

import domain.model.telemetry.FleetTrackingCriteria
import domain.repository.WarehouseRepository
import domain.usecase.routing.FindOptimalPathUseCase
import domain.usecase.telemetry.TrackActiveFleetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext

private const val MAX_TELEMETRY_NOTIFICATIONS = 10

internal class AsyncCliShell(
    private val warehouseRepository: WarehouseRepository,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
    private val trackActiveFleetUseCase: TrackActiveFleetUseCase,
    private val packageClassificationCli: PackageClassificationCli,
) {

    suspend fun run() = coroutineScope {
        while (true) {
            CliMenu.printMainMenu()

            val option = withContext(Dispatchers.IO) {
                readlnOrNull()?.trim()
            }

            when (option) {
                "1" -> runRouteCalculation(
                    warehouseRepository,
                    findOptimalPathUseCase
                )

                "2" -> packageClassificationCli.run()

                "3" -> streamTelemetry()

                "0", null -> {
                    CliMenu.printExitMessage()
                    break
                }

                else -> CliMenu.printInvalidOption()
            }

            CliMenu.printSeparator()
        }

        CliMenu.printGoodbye()
    }

    private suspend fun streamTelemetry() {
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
