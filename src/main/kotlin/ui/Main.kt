package ui

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.builder.RepositoryProvider
import domain.model.Warehouse
import domain.pricing.EcoStrategy
import domain.pricing.RoutePricingEngine
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

    val repositories = initializeRepositories()
    val graph = buildDomainGraph(repositories)
    val assignPackagesUseCase = createPackageConsolidationUseCase(repositories)
    val findOptimalPathUseCase = FindOptimalPathUseCase(OptimalTransitRouter(repositories.warehouseRepository))
    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(LeastHopRouter(repositories.warehouseRepository))
    val findBidirectionalRouteUseCase =
        FindBidirectionalRouteUseCase(BidirectionalBfsRouter(repositories.warehouseRepository))
    val calculatePricingUseCase = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))

    runCargoDemos(repositories, graph)
    runPackageConsolidationDemo(assignPackagesUseCase)
    runPricingAndDecoratorDemos(graph, calculatePricingUseCase)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(
        repositories, graph, findOptimalPathUseCase, findFewestHopsRouteUseCase, findBidirectionalRouteUseCase
    )

    runSimulationDemos(graph)
    printSystemFooter()
}

private fun runSimulationDemos(graph: List<Warehouse>) {
    printTreePerformanceAnalysis(AnalyzeTreePerformanceUseCase(), DEFAULT_PACKAGE_COUNT)
    printCommandPatternTest(
        dispatchVehicleUseCase = DispatchVehicleUseCase(),
        firstWarehouse = graph.first(),
        firstVehicle = graph.first().stationedVehicles.first()
    )
    printNetworkResilienceAnalysis(CalculateNetworkResilienceScoreUseCase(), graph)
}

private fun createPackageConsolidationUseCase(
    repositories: RepositoryProvider
): AssignPackagesToAvailableVehicleUseCase {
    return AssignPackagesToAvailableVehicleUseCase(
        findPackagesForConsolidationUseCase =
            FindPackagesForConsolidationUseCase(
                repositories.packageRepository
            ),
        vehicleRepository = repositories.vehicleRepository
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