package ui

import data.repository.CsvPackageRepository
import data.repository.CsvRouteRepository
import data.repository.CsvVehicleRepository
import data.repository.CsvWarehouseRepository
import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.model.Warehouse
import domain.pricing.EcoStrategy
import domain.pricing.RoutePricingEngine
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import domain.usecase.AnalyzeTreePerformanceUseCase
import domain.usecase.AssignPackagesToAvailableVehicleUseCase
import domain.usecase.CalculateNetworkResilienceScoreUseCase
import domain.usecase.CalculatePricingUseCase
import domain.usecase.DispatchVehicleUseCase
import domain.usecase.FindBidirectionalRouteUseCase
import domain.usecase.FindFewestHopsRouteUseCase
import domain.usecase.FindOptimalPathUseCase
import domain.usecase.FindPackagesForConsolidationUseCase

private const val DEFAULT_PACKAGE_COUNT = 1000

fun main() {
    printSystemHeader()

    val warehouseRepository = createWarehouseRepository()
    val vehicleRepository = createVehicleRepository(warehouseRepository)
    val packageRepository = createPackageRepository(warehouseRepository)
    val routeRepository = createRouteRepository(warehouseRepository)

    printParsingReport(
        warehouseRepository, vehicleRepository, packageRepository, routeRepository
    )

    val graph = buildDomainGraph(
        warehouseRepository, vehicleRepository, packageRepository, routeRepository
    )

    runDemos(warehouseRepository, vehicleRepository, packageRepository, graph)

    printSystemFooter()
}

private fun runDemos(
    warehouseRepository: WarehouseRepository,
    vehicleRepository: VehicleRepository,
    packageRepository: PackageRepository,
    graph: List<Warehouse>
) {
    runCargoAndPricingDemos(packageRepository, vehicleRepository, graph)
    runRoutingDemos(warehouseRepository, graph)
    runSimulationDemos(graph)
}

private fun runCargoAndPricingDemos(
    packageRepository: PackageRepository,
    vehicleRepository: VehicleRepository,
    graph: List<Warehouse>
) {
    runCargoDemos(packageRepository, graph)

    runPackageConsolidationDemo(
        createPackageConsolidationUseCase(packageRepository, vehicleRepository)
    )

    runPricingAndDecoratorDemos(
        graph,
        CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))
    )

    runBreakdownSimulationDemo()
}

private fun runRoutingDemos(
    warehouseRepository: WarehouseRepository,
    graph: List<Warehouse>
) {
    runRoutingAndComparisonDemos(
        warehouseRepository,
        graph,
        FindOptimalPathUseCase(OptimalTransitRouter(warehouseRepository)),
        FindFewestHopsRouteUseCase(LeastHopRouter(warehouseRepository)),
        FindBidirectionalRouteUseCase(BidirectionalBfsRouter(warehouseRepository))
    )
}

private fun createWarehouseRepository(): WarehouseRepository {
    return CsvWarehouseRepository(WAREHOUSES_FILE_PATH)
}

private fun createVehicleRepository(
    warehouseRepository: WarehouseRepository
): VehicleRepository {
    return CsvVehicleRepository(
        filePath = VEHICLES_FILE_PATH,
        warehouseRepository = warehouseRepository
    )
}

private fun createPackageRepository(
    warehouseRepository: WarehouseRepository
): PackageRepository {
    return CsvPackageRepository(
        filePath = PACKAGE_FILE_PATH,
        warehouseRepository = warehouseRepository
    )
}

private fun createRouteRepository(
    warehouseRepository: WarehouseRepository
): RouteRepository {
    return CsvRouteRepository(
        filePath = ROUTES_FILE_PATH,
        warehouseRepository = warehouseRepository
    )
}

private fun runSimulationDemos(
    graph: List<Warehouse>
) {
    printTreePerformanceAnalysis(
        AnalyzeTreePerformanceUseCase(),
        DEFAULT_PACKAGE_COUNT
    )

    printCommandPatternTest(
        dispatchVehicleUseCase = DispatchVehicleUseCase(),
        firstWarehouse = graph.first(),
        firstVehicle = graph.first().stationedVehicles.first()
    )

    printNetworkResilienceAnalysis(
        CalculateNetworkResilienceScoreUseCase(),
        graph
    )
}

private fun createPackageConsolidationUseCase(
    packageRepository: PackageRepository,
    vehicleRepository: VehicleRepository
): AssignPackagesToAvailableVehicleUseCase {
    return AssignPackagesToAvailableVehicleUseCase(
        findPackagesForConsolidationUseCase = FindPackagesForConsolidationUseCase(packageRepository),
        vehicleRepository = vehicleRepository
    )
}

private fun printNetworkResilienceAnalysis(
    calculateNetworkResilienceScoreUseCase: CalculateNetworkResilienceScoreUseCase,
    graph: List<Warehouse>
) {
    println("\n[NETWORK RESILIENCE ANALYSIS]")
    println("============================================================")

    val resilienceScore = calculateNetworkResilienceScoreUseCase(graph)

    println("Network Resilience Score: $resilienceScore")
    println("============================================================")
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
