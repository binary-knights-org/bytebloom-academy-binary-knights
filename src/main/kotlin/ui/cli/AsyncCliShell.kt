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
    private val searchCli: SearchCli,
) {

    suspend fun run() = coroutineScope {
        while (true) {
            printMenu()

            val option = withContext(Dispatchers.IO) {
                readlnOrNull()?.trim()
            }

            when (option) {
                "1" -> runRouteCalculation(warehouseRepository, findOptimalPathUseCase)

                "2" -> packageClassificationCli.run()

                "3" -> streamTelemetry()

                "4" -> searchCli.run()

                "0", null -> {
                    println("\u001B[95mExiting Logistics CLI...")
                    break
                }

                else -> {
                    println(
                        "\u001B[31m" +
                                "Invalid option. Please choose 0, 1, 2, 3, or 4."
                    )
                }
            }

            println("\u001B[90m-----------------------------------------------")
        }

        println("\u001B[95mGoodbye!")
    }

    private suspend fun streamTelemetry() {
        println("\u001B[36mTelemetry streaming started.")
        println(
            "\u001B[95m" +
                    "Displaying up to $MAX_TELEMETRY_NOTIFICATIONS " +
                    "delay notifications..."
        )

        var notificationCount = 0

        withLoadingIndicator("Waiting for telemetry") { stopLoading ->
            trackActiveFleetUseCase(
                criteria = FleetTrackingCriteria()
            )
                .take(MAX_TELEMETRY_NOTIFICATIONS)
                .collect { notification ->
                    stopLoading()
                    notificationCount++

                    println(
                        "[Telemetry]" +
                                "Vehicle: ${notification.vehicleId} | " +
                                "Delay: ${notification.delayMinutes} min | " +
                                "Speed: ${notification.currentSpeedKmh} km/h | " +
                                "Fuel: ${notification.fuelPercentage}% | " +
                                "Reason: ${notification.reason}"
                    )
                }
        }

        println(
            "\u001B[95m" +
                    "Telemetry display completed. " +
                    "Notifications: $notificationCount"
        )
    }

    private fun printMenu() {
        println(
            "\u001B[34m" +
                    "1. Calculate Route\n" +
                    "2. Classify Package via KNN\n" +
                    "3. Real-time Telemetry Streaming Logs (10 notifications)\n" +
                    "4. Search (Packages / Routes / Vehicles / Warehouses)\n" +
                    "0. Exit"
        )

        print("\u001B[35mChoose an option: ")
    }
}
