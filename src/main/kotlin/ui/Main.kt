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
import data.remote.client.SupabaseHttpClient
import data.remote.supabase.SupabaseWarehouseDataSource
import data.remote.supabase.SupabasePackageDataSource
import data.remote.supabase.SupabaseRouteDataSource
import data.remote.supabase.SupabaseVehicleDataSource
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
   printSystemHeader()
   val warehouseDataSource = SupabaseWarehouseDataSource(SupabaseHttpClient)
   val packageDataSource = SupabasePackageDataSource(SupabaseHttpClient)
   val vehicleDataSource = SupabaseVehicleDataSource(SupabaseHttpClient)
   val routeDataSource = SupabaseRouteDataSource(SupabaseHttpClient)
   val warehouseRepository =
       WarehouseRepositoryImpl(warehouseDataSource, packageDataSource, vehicleDataSource, routeDataSource)
   val packageRepository = PackageRepositoryImpl(packageDataSource, warehouseRepository)
   val vehicleRepository = VehicleRepositoryImpl(vehicleDataSource, warehouseRepository)
   val routeRepository = RouteRepositoryImpl(routeDataSource, warehouseRepository)
   printParsingReport(vehicleRepository, warehouseRepository, packageRepository, routeRepository)
   val warehouses = buildDomainGraph(warehouseRepository)
   val findPackagesForConsolidationUseCase = FindPackagesForConsolidationUseCase(packageRepository)
   val findSuitableVehicleUseCase = FindSuitableVehicleUseCase(vehicleRepository)
   val assignPackagesToVehicleUseCase = AssignPackagesToVehicleUseCase(findPackagesForConsolidationUseCase,
       findSuitableVehicleUseCase)
   val findOptimalPathUseCase = FindOptimalPathUseCase(OptimalTransitRouter(warehouseRepository))
   val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(LeastHopRouter(warehouseRepository))
   val findBidirectionalRouteUseCase = FindBidirectionalRouteUseCase(BidirectionalBfsRouter(warehouseRepository))
   val calculatePricingUseCase = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))
   runCargoDemos(packageRepository, warehouses)
    runPackageConsolidationDemo(assignPackagesToVehicleUseCase)
   runPricingAndDecoratorDemos(warehouses, calculatePricingUseCase)
   runBreakdownSimulationDemo()
   runRoutingAndComparisonDemos(
       warehouseRepository, warehouses,
       findOptimalPathUseCase, findFewestHopsRouteUseCase, findBidirectionalRouteUseCase
   )
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
