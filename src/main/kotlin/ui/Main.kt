package ui

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.pricing.EcoStrategy
import domain.pricing.RoutePricingEngine
import domain.usecase.analytics.CalculatePricingUseCase
import domain.usecase.routing.FindBidirectionalRouteUseCase
import domain.usecase.routing.FindFewestHopsRouteUseCase
import domain.usecase.routing.FindOptimalPathUseCase
import domain.usecase.shipment.FindPackagesForConsolidationUseCase
import domain.usecase.vehicle.FindSuitableVehicleUseCase
import domain.usecase.vehicle.AssignPackagesToVehicleUseCase

fun main() {
    printSystemHeader()

    val warehouseRepository = data.repository.CsvWarehouseRepository(WAREHOUSES_FILE_PATH)
    val packageRepository = data.repository.CsvPackageRepository(PACKAGE_FILE_PATH, warehouseRepository)
    val vehicleRepository = data.repository.CsvVehicleRepository(VEHICLES_FILE_PATH, warehouseRepository)
    val routeRepository = data.repository.CsvRouteRepository(ROUTES_FILE_PATH, warehouseRepository)

    printParsingReport(vehicleRepository, warehouseRepository, packageRepository, routeRepository)

    val graph = buildDomainGraph(vehicleRepository, warehouseRepository, packageRepository, routeRepository)
    val findPackagesForConsolidationUseCase = FindPackagesForConsolidationUseCase(packageRepository)
    val findSuitableVehicleUseCase = FindSuitableVehicleUseCase(vehicleRepository)
    val assignPackagesToVehicleUseCase = AssignPackagesToVehicleUseCase()
    val findOptimalPathUseCase = FindOptimalPathUseCase(OptimalTransitRouter(warehouseRepository))
    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(LeastHopRouter(warehouseRepository))
    val findBidirectionalRouteUseCase = FindBidirectionalRouteUseCase(BidirectionalBfsRouter(warehouseRepository))
    val calculatePricingUseCase = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))

    runCargoDemos(packageRepository, graph)
    runPackageConsolidationDemo(
        findPackagesForConsolidationUseCase, findSuitableVehicleUseCase, assignPackagesToVehicleUseCase)
    runPricingAndDecoratorDemos(graph, calculatePricingUseCase)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(
        warehouseRepository, graph, findOptimalPathUseCase, findFewestHopsRouteUseCase, findBidirectionalRouteUseCase)
    runSimulationDemos(vehicleRepository, warehouseRepository, graph)
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
