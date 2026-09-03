package ui

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.builder.RepositoryProvider
import domain.usecase.FindBidirectionalRouteUseCase
import domain.pricing.EcoStrategy
import domain.pricing.RoutePricingEngine
import domain.usecase.AnalyzeTreePerformanceUseCase
import domain.usecase.CalculatePricingUseCase
import domain.usecase.DispatchVehicleUseCase
import domain.usecase.FindFewestHopsRouteUseCase
import domain.usecase.FindOptimalPathUseCase
import domain.usecase.AssignPackagesToAvailableVehicleUseCase
import domain.usecase.FindPackagesForConsolidationUseCase
private const val DEFAULT_PACKAGE_COUNT = 1000

fun main() {
    printSystemHeader()

    val repositories = initializeRepositories()
    val graph = buildDomainGraph(repositories)
    val assignPackagesToAvailableVehicleUseCase =
        AssignPackagesToAvailableVehicleUseCase(findPackagesForConsolidationUseCase =
                FindPackagesForConsolidationUseCase(
                    repositories.packageRepository ),
            vehicleRepository = repositories.vehicleRepository
        )
    val findOptimalPathUseCase = FindOptimalPathUseCase(OptimalTransitRouter(repositories.warehouseRepository))
    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(LeastHopRouter(repositories.warehouseRepository))
    val findBidirectionalRouteUseCase =
        FindBidirectionalRouteUseCase(BidirectionalBfsRouter(repositories.warehouseRepository))
    val calculatePricingUseCase = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))
    val analyzeTreePerformanceUseCase = AnalyzeTreePerformanceUseCase()

    runCargoDemos(repositories, graph)
    runPackageConsolidationDemo(assignPackagesToAvailableVehicleUseCase)
    runPricingAndDecoratorDemos(graph, calculatePricingUseCase)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(
        repositories, graph, findOptimalPathUseCase, findFewestHopsRouteUseCase, findBidirectionalRouteUseCase)
    printTreePerformanceAnalysis(
        analyzeTreePerformanceUseCase,
        DEFAULT_PACKAGE_COUNT)
    printCommandPatternTest(
        dispatchVehicleUseCase = DispatchVehicleUseCase(),
        firstWarehouse = graph.first() ,
        firstVehicle = graph.first().stationedVehicles.first())
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

