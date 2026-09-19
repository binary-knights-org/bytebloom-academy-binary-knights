package ui

import data.local.csv.CsvFileHandler
import data.local.csv.CsvPackageDataSourceImpl
import data.local.csv.CsvRouteDataSourceImpl
import data.local.csv.CsvVehicleDataSourceImpl
import data.local.csv.CsvWarehouseDataSourceImpl
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
import data.remote.supabase.SupabaseWarehouseDataSourceImpl
import data.remote.supabase.SupabasePackageDataSourceImpl
import data.remote.supabase.SupabaseRouteDataSourceImpl
import data.remote.supabase.SupabaseVehicleDataSourceImpl
import data.repository.LocalDataSources
import data.repository.RemoteDataSources
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
   printSystemHeader()
   val warehouseDataSource = SupabaseWarehouseDataSourceImpl(SupabaseHttpClient)
   val packageDataSource = SupabasePackageDataSourceImpl(SupabaseHttpClient)
   val vehicleDataSource = SupabaseVehicleDataSourceImpl(SupabaseHttpClient)
   val routeDataSource = SupabaseRouteDataSourceImpl(SupabaseHttpClient)

    val warehouseCsvHandler = CsvFileHandler("src/main/resources/warehouses.csv")
    val packageCsvHandler = CsvFileHandler("src/main/resources/packages.csv")
    val vehicleCsvHandler = CsvFileHandler("src/main/resources/fleet.csv")
    val routeCsvHandler = CsvFileHandler("src/main/resources/routes.csv")

    val localWarehouseDataSource = CsvWarehouseDataSourceImpl(warehouseCsvHandler)
    val localPackageDataSource = CsvPackageDataSourceImpl(packageCsvHandler)
    val localVehicleDataSource = CsvVehicleDataSourceImpl(vehicleCsvHandler)
    val localRouteDataSource = CsvRouteDataSourceImpl(routeCsvHandler)

    val remoteSources = RemoteDataSources(
        warehouseDataSource,
        packageDataSource,
        vehicleDataSource,
        routeDataSource
    )

    val localSources = LocalDataSources(
        localWarehouseDataSource,
        localPackageDataSource,
        localVehicleDataSource,
        localRouteDataSource
    )

    val warehouseRepository = WarehouseRepositoryImpl(remoteSources, localSources,)
   val packageRepository = PackageRepositoryImpl(packageDataSource,localPackageDataSource , warehouseRepository)
   val vehicleRepository = VehicleRepositoryImpl(vehicleDataSource,localVehicleDataSource , warehouseRepository)
   val routeRepository = RouteRepositoryImpl(routeDataSource,localRouteDataSource , warehouseRepository)

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
