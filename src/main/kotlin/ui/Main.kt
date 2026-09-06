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
import domain.repository.VehicleRepository
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

    val warehouseRepository =
        CsvWarehouseRepository(WAREHOUSES_FILE_PATH)

    val vehicleRepository =
        CsvVehicleRepository(
            filePath = VEHICLES_FILE_PATH,
            warehouseRepository = warehouseRepository
        )

    val packageRepository =
        CsvPackageRepository(
            filePath = PACKAGE_FILE_PATH,
            warehouseRepository = warehouseRepository
        )

    val routeRepository =
        CsvRouteRepository(
            filePath = ROUTES_FILE_PATH,
            warehouseRepository = warehouseRepository
        )

    printParsingReport(
        warehouseRepository,
        vehicleRepository,
        packageRepository,
        routeRepository
    )

    val graph = buildDomainGraph(
        warehouseRepository,
        vehicleRepository,
        packageRepository,
        routeRepository
    )

    val assignPackagesUseCase = createPackageConsolidationUseCase(
        packageRepository,
        vehicleRepository
    )

    val findOptimalPathUseCase =
        FindOptimalPathUseCase(OptimalTransitRouter(warehouseRepository))

    val findFewestHopsRouteUseCase =
        FindFewestHopsRouteUseCase(LeastHopRouter(warehouseRepository))

    val findBidirectionalRouteUseCase =
        FindBidirectionalRouteUseCase(BidirectionalBfsRouter(warehouseRepository))

    val calculatePricingUseCase =
        CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))

    runCargoDemos(packageRepository, graph)
    runPackageConsolidationDemo(assignPackagesUseCase)
    runPricingAndDecoratorDemos(graph, calculatePricingUseCase)
    runBreakdownSimulationDemo()

    runRoutingAndComparisonDemos(
        warehouseRepository,
        graph,
        findOptimalPathUseCase,
        findFewestHopsRouteUseCase,
        findBidirectionalRouteUseCase
    )

    runSimulationDemos(graph)
    printSystemFooter()
}

private fun runSimulationDemos(graph: List<Warehouse>) {
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
        findPackagesForConsolidationUseCase =
            FindPackagesForConsolidationUseCase(packageRepository),
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