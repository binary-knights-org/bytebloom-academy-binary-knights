package ui

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.usecase.FindBidirectionalRouteUseCase
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

    runCargoDemos(getAllPackagesUseCase, graph)
    runPricingAndDecoratorDemos(graph)
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
