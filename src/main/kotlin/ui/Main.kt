package ui

import di.networkModule
import di.pricingModule
import di.repositoryModule
import di.routingModule
import di.useCaseModule
import di.validatorModule
import di.telemetryModule
import domain.repository.WarehouseRepository
import domain.usecase.analytics.ClassifyPackageSectorUseCase
import domain.usecase.routing.FindOptimalPathUseCase
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import domain.usecase.telemetry.TrackActiveFleetUseCase
import domain.repository.PackageRepository

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

    val warehouseRepository = koin.get<WarehouseRepository>()
    val findOptimalPathUseCase = koin.get<FindOptimalPathUseCase>()
    val packageClassificationCli = PackageClassificationCli(
        packageRepository = koin.get<PackageRepository>(),
        classifyPackageSectorUseCase =
            koin.get<ClassifyPackageSectorUseCase>()
    )

    val cliShell = AsyncCliShell(
        warehouseRepository = warehouseRepository,
        findOptimalPathUseCase = findOptimalPathUseCase,
        trackActiveFleetUseCase = koin.get<TrackActiveFleetUseCase>(),
        packageClassificationCli = packageClassificationCli
    )

    cliShell.run()

    printSystemFooter()
}

private fun printSystemHeader() {
    println("\u001B[34m"+"\n============================================================")
    println("\u001B[34m"+"      BYTEBLOOM ACADEMY: LOGISTICS & ROUTING ENGINE         ")
    println("\u001B[34m"+"============================================================")
}

private fun printSystemFooter() {
    println("\u001B[34m"+"\n============================================================")
    println("\u001B[34m"+"          SYSTEM EXECUTION COMPLETED SUCCESSFULLY           ")
    println("\u001B[34m"+"============================================================\n")
}
