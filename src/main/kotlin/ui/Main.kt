package ui

import data.repository.PackageRepositoryImpl
import data.repository.RouteRepositoryImpl
import data.repository.VehicleRepositoryImpl
import data.repository.WarehouseRepositoryImpl
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
    val warehouseRepository =
        WarehouseRepositoryImpl(WAREHOUSES_FILE_PATH, PACKAGE_FILE_PATH, VEHICLES_FILE_PATH, ROUTES_FILE_PATH)
    val packageRepository = PackageRepositoryImpl(PACKAGE_FILE_PATH, warehouseRepository)
    val vehicleRepository = VehicleRepositoryImpl(VEHICLES_FILE_PATH, warehouseRepository)
    val routeRepository = RouteRepositoryImpl(ROUTES_FILE_PATH, warehouseRepository)
    printParsingReport(vehicleRepository, warehouseRepository, packageRepository, routeRepository)
    val warehouses = buildDomainGraph(warehouseRepository)
    val findPackagesForConsolidationUseCase = FindPackagesForConsolidationUseCase(packageRepository)
    val findSuitableVehicleUseCase = FindSuitableVehicleUseCase(vehicleRepository)
    val assignPackagesToVehicleUseCase = AssignPackagesToVehicleUseCase()
    val findOptimalPathUseCase = FindOptimalPathUseCase(OptimalTransitRouter(warehouseRepository))
    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(LeastHopRouter(warehouseRepository))
    val findBidirectionalRouteUseCase = FindBidirectionalRouteUseCase(BidirectionalBfsRouter(warehouseRepository))
    val calculatePricingUseCase = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))
    runCargoDemos(packageRepository, warehouses)
    runPackageConsolidationDemo(
        findPackagesForConsolidationUseCase, findSuitableVehicleUseCase, assignPackagesToVehicleUseCase)
    runPricingAndDecoratorDemos(warehouses, calculatePricingUseCase)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(warehouseRepository, warehouses,
        findOptimalPathUseCase, findFewestHopsRouteUseCase, findBidirectionalRouteUseCase)
    runSimulationDemos(vehicleRepository, warehouseRepository, warehouses)
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
