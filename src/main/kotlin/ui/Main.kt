package ui

import di.networkModule
import di.pricingModule
import di.repositoryModule
import di.routingModule
import di.useCaseModule
import di.validatorModule

import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

import domain.usecase.analytics.CalculatePricingUseCase
import domain.usecase.routing.FindBidirectionalRouteUseCase
import domain.usecase.routing.FindFewestHopsRouteUseCase
import domain.usecase.routing.FindOptimalPathUseCase
import domain.usecase.vehicle.AssignPackagesToVehicleUseCase

import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin


fun main() = runBlocking {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            pricingModule,
            routingModule,
            useCaseModule
        )
    }.koin

    printSystemHeader()

    val warehouseRepository = koin.get<WarehouseRepository>()
    val packageRepository = koin.get<PackageRepository>()
    val vehicleRepository = koin.get<VehicleRepository>()
    val routeRepository = koin.get<RouteRepository>()

    printParsingReport(vehicleRepository, warehouseRepository, packageRepository, routeRepository)

    val warehouses = buildDomainGraph(warehouseRepository)
    val vehicles = vehicleRepository.getAll()

    val assignPackagesToVehicleUseCase = koin.get<AssignPackagesToVehicleUseCase>()
    val findOptimalPathUseCase = koin.get<FindOptimalPathUseCase>()
    val findFewestHopsRouteUseCase = koin.get<FindFewestHopsRouteUseCase>()
    val findBidirectionalRouteUseCase = koin.get<FindBidirectionalRouteUseCase>()
    val calculatePricingUseCase = koin.get<CalculatePricingUseCase>()

    runCargoDemos(packageRepository, warehouses)
    runPackageConsolidationDemo(assignPackagesToVehicleUseCase)
    runPricingAndDecoratorDemos(warehouses, calculatePricingUseCase)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(
        warehouseRepository,
        warehouses,
        findOptimalPathUseCase,
        findFewestHopsRouteUseCase,
        findBidirectionalRouteUseCase
    )
    runSimulationDemos(vehicleRepository, warehouseRepository, warehouses)
    runGreedyFleetDemo(vehicles)
    runInvalidInputDemo()
    printSystemFooter()
}

private fun printSystemHeader() {
    println(
        """
        
    ========================================================================
                                                                          
              BYTEBLOOM ACADEMY: LOGISTICS & ROUTING ENGINE        
                                                                          
    ========================================================================
    """.trimIndent()
    )
}

private fun printSystemFooter() {
    println(
        """
        
    ========================================================================
                   SYSTEM EXECUTION COMPLETED SUCCESSFULLY              
    ========================================================================
    
    """.trimIndent()
    )
}
