package ui.cli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

internal class AsyncCliShell(
    private val routingCli: RoutingCli,
    private val telemetryCli: TelemetryCli,
    private val packageClassificationCli: PackageClassificationCli,
) {
    suspend fun run() = coroutineScope {
        while (true) {
            CliMenu.printMainMenu()

            val option = withContext(Dispatchers.IO) {
                readlnOrNull()?.trim()
            }

            when (option) {
                "1" -> routingCli.run()
                "2" -> packageClassificationCli.run()
                "3" -> telemetryCli.run()
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
}
