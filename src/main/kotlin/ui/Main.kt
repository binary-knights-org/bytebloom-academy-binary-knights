package ui

import di.networkModule
import di.pricingModule
import di.repositoryModule
import di.routingModule
import di.searchModule
import di.telemetryModule
import di.useCaseModule
import di.validatorModule
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository
import domain.usecase.analytics.ClassifyPackageSectorUseCase
import domain.usecase.routing.FindOptimalPathUseCase
import domain.usecase.telemetry.TrackActiveFleetUseCase
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import ui.cli.AsyncCliShell
import ui.cli.LivePicker
import ui.cli.PackageClassificationCli
import ui.cli.SearchCli

fun main() = runBlocking {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            pricingModule,
            routingModule,
            useCaseModule,
            searchModule,
            telemetryModule
        )
    }.koin

    printSystemHeader()

    val warehouseRepository = koin.get<WarehouseRepository>()
    val findOptimalPathUseCase = koin.get<FindOptimalPathUseCase>()
    val packageClassificationCli = PackageClassificationCli(
        packageRepository = koin.get<PackageRepository>(),
        classifyPackageSectorUseCase = koin.get<ClassifyPackageSectorUseCase>()
    )

    val searchCli = SearchCli(
        picker = LivePicker(),
        searchPackages = koin.get(),
        searchRoutes = koin.get(),
        searchVehicles = koin.get(),
        searchWarehouses = koin.get()
    )

    val cliShell = AsyncCliShell(
        warehouseRepository = warehouseRepository,
        findOptimalPathUseCase = findOptimalPathUseCase,
        trackActiveFleetUseCase = koin.get<TrackActiveFleetUseCase>(),
        packageClassificationCli = packageClassificationCli,
        searchCli = searchCli
    )

    cliShell.run()

    printSystemFooter()
}

private fun printSystemHeader() {
    println("\u001B[34m\n \$\$\$\$\$\$\$\\  \$\$\\                                         \n" +
            "\$\$  __\$\$\\ \\__|                                        \n" +
            "\$\$ |  \$\$ |\$\$\\ \$\$\$\$\$\$\$\\   \$\$\$\$\$\$\\   \$\$\$\$\$\$\\  \$\$\\   \$\$\\ \n" +
            "\$\$\$\$\$\$\$\\ |\$\$ |\$\$  __\$\$\\  \\____\$\$\\ \$\$  __\$\$\\ \$\$ |  \$\$ |\n" +
            "\$\$  __\$\$\\ \$\$ |\$\$ |  \$\$ | \$\$\$\$\$\$\$ |\$\$ |  \\__|\$\$ |  \$\$ |\n" +
            "\$\$ |  \$\$ |\$\$ |\$\$ |  \$\$ |\$\$  __\$\$ |\$\$ |      \$\$ |  \$\$ |\n" +
            "\$\$\$\$\$\$\$  |\$\$ |\$\$ |  \$\$ |\\\$\$\$\$\$\$\$ |\$\$ |      \\\$\$\$\$\$\$\$ |\n" +
            "\\_______/ \\__|\\__|  \\__| \\_______|\\__|       \\____\$\$ |\n" +
            "                                            \$\$\\   \$\$ |\n" +
            "                                            \\\$\$\$\$\$\$  |\n" +
            "                                             \\______/ \n" +
            "\$\$\\   \$\$\\           \$\$\\           \$\$\\        \$\$\\      \n" +
            "\$\$ | \$\$  |          \\__|          \$\$ |       \$\$ |     \n" +
            "\$\$ |\$\$  / \$\$\$\$\$\$\$\\  \$\$\\  \$\$\$\$\$\$\\  \$\$\$\$\$\$\$\\ \$\$\$\$\$\$\\    \n" +
            "\$\$\$\$\$  /  \$\$  __\$\$\\ \$\$ |\$\$  __\$\$\\ \$\$  __\$\$\\\\_\$\$  _|   \n" +
            "\$\$  \$\$<   \$\$ |  \$\$ |\$\$ |\$\$ /  \$\$ |\$\$ |  \$\$ | \$\$ |     \n" +
            "\$\$ |\\\$\$\\  \$\$ |  \$\$ |\$\$ |\$\$ |  \$\$ |\$\$ |  \$\$ | \$\$ |\$\$\\  \n" +
            "\$\$ | \\\$\$\\ \$\$ |  \$\$ |\$\$ |\\\$\$\$\$\$\$\$ |\$\$ |  \$\$ | \\\$\$\$\$  | \n" +
            "\\__|  \\__|\\__|  \\__|\\__| \\____\$\$ |\\__|  \\__|  \\____/  \n" +
            "                        \$\$\\   \$\$ |                    \n" +
            "                        \\\$\$\$\$\$\$  |                    \n" +
            "                         \\______/                      ")

    println("\u001B[34m\n============================================================")
    println("\u001B[34m      BYTEBLOOM ACADEMY: LOGISTICS & ROUTING ENGINE         ")
    println("\u001B[34m============================================================")
}

private fun printSystemFooter() {
    println("\u001B[34m\n============================================================")
    println("\u001B[34m          SYSTEM EXECUTION COMPLETED SUCCESSFULLY           ")
    println("\u001B[34m============================================================\n")
}
