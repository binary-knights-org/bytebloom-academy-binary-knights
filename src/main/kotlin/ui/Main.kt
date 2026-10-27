package ui

import di.networkModule
import di.pricingModule
import di.repositoryModule
import di.routingModule
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
import ui.cli.PackageClassificationCli
import ui.cli.RoutingCli
import ui.cli.TelemetryCli

fun main() = runBlocking {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            pricingModule,
            routingModule,
            useCaseModule,
            telemetryModule
        )
    }.koin

    printSystemHeader()

    val cliShell = AsyncCliShell(
        routingCli = RoutingCli(
            warehouseRepository = koin.get<WarehouseRepository>(),
            findOptimalPathUseCase = koin.get<FindOptimalPathUseCase>()
        ),
        packageClassificationCli = PackageClassificationCli(
            packageRepository = koin.get<PackageRepository>(),
            classifyPackageSectorUseCase = koin.get<ClassifyPackageSectorUseCase>()
        ),
        telemetryCli = TelemetryCli(
            trackActiveFleetUseCase = koin.get<TrackActiveFleetUseCase>()
        )
    )

    cliShell.run()

    printSystemFooter()
}

private fun printSystemHeader() {
    println(
        "\u001B[34m\n" +
            "============================================================\n" +
            "      BYTEBLOOM ACADEMY: LOGISTICS & ROUTING ENGINE         \n" +
            "============================================================"
    )
}

private fun printSystemFooter() {
    println(
        "\u001B[34m\n" +
            "============================================================\n" +
            "          SYSTEM EXECUTION COMPLETED SUCCESSFULLY           \n" +
            "============================================================\n"
    )
}
