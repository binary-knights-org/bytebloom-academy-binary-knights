package ui

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.usecase.FindBidirectionalRouteUseCase
import domain.pricing.EcoStrategy
import domain.pricing.RoutePricingEngine
import domain.usecase.CalculatePricingUseCase
import domain.usecase.FindFewestHopsRouteUseCase
import domain.usecase.FindOptimalPathUseCase
import domain.usecase.GetAllPackagesUseCase

fun main() {
    printSystemHeader()

    val repositories = initializeRepositories()
    val graph = buildDomainGraph(repositories)

    val findOptimalPathUseCase = FindOptimalPathUseCase(OptimalTransitRouter(repositories.warehouseRepository))
    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(LeastHopRouter(repositories.warehouseRepository))
    val findBidirectionalRouteUseCase =
        FindBidirectionalRouteUseCase(BidirectionalBfsRouter(repositories.warehouseRepository))
    val getAllPackagesUseCase = GetAllPackagesUseCase(repositories.packageRepository)
    val calculatePricingUseCase = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))

    runCargoDemos(getAllPackagesUseCase, graph)
    runPricingAndDecoratorDemos(graph, calculatePricingUseCase)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(
        repositories, graph, findOptimalPathUseCase, findFewestHopsRouteUseCase, findBidirectionalRouteUseCase
    )

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
