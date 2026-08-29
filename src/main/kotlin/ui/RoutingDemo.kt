package ui

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.builder.RepositoryProvider
import domain.model.Warehouse
import domain.usecase.FindBidirectionalRouteUseCase
import domain.usecase.FindFewestHopsRouteUseCase
import domain.usecase.FindOptimalPathUseCase
import java.util.*

internal const val NANOS_TO_MILLIS = 1_000_000.0
internal const val PERCENTAGE_MULTIPLIER = 100.0
private const val ROUTER_LABEL_PADDING = 35

internal data class RoutingResult(
    val path: List<Warehouse>?,
    val executionTime: Double,
    val evaluatedWarehouses: Int,
    val totalDistanceKm: Double
)

internal fun runRoutingAndComparisonDemos(
    repositories: RepositoryProvider,
    graph: List<Warehouse>,
    findOptimalPathUseCase: FindOptimalPathUseCase,
    findFewestHopsRouteUseCase: FindFewestHopsRouteUseCase,
    findBidirectionalRouteUseCase: FindBidirectionalRouteUseCase
) {
    println("\n[PATHFINDING ALGORITHMS]")
    println("============================================================")
    printRouteDemo(graph, findFewestHopsRouteUseCase::invoke, "Least-Hop Router (Standard BFS)")
    printRouteDemo(graph, findBidirectionalRouteUseCase::invoke, "Bidirectional BFS Router")
    printRouteDemo(graph, findOptimalPathUseCase::invoke, "Optimal Transit Router (Dijkstra)")
    compareRoutingAlgorithms(repositories, graph)
}

private fun calculateTotalDistance(path: List<Warehouse>?): Double {
    if (path == null || path.size < 2) return 0.0
    var distance = 0.0
    for (i in 0 until path.size - 1) {
        val current = path[i]
        val next = path[i + 1]
        val route = current.outgoingRoutes.find { it.destinationHub.id == next.id }
        if (route != null) distance += route.distanceKm
    }
    return distance
}

private fun printRouteDemo(
    graph: List<Warehouse>,
    findPath: (Warehouse, Warehouse) -> List<Warehouse>?,
    label: String
) {
    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return

    val path = findPath(origin, destination)
    print(" ${label.padEnd(ROUTER_LABEL_PADDING)} -> ")

    if (path == null) println("No Path") else {
        val distance = calculateTotalDistance(path)
        println("${path.size - 1} Hops | %.2f km".format(Locale.US, distance))
    }
}

private fun runStandardBfs(
    repositories: RepositoryProvider,
    origin: Warehouse,
    destination: Warehouse
): RoutingResult {
    val router = LeastHopRouter(repositories.warehouseRepository)
    val startTime = System.nanoTime()
    val path = router.findShortestPath(origin, destination)
    return RoutingResult(
        path,
        (System.nanoTime() - startTime) / NANOS_TO_MILLIS,
        router.visitedWarehouseCount,
        calculateTotalDistance(path)
    )
}

private fun runBidirectionalBfs(
    repositories: RepositoryProvider,
    origin: Warehouse,
    destination: Warehouse
): RoutingResult {
    val router = BidirectionalBfsRouter(repositories.warehouseRepository)
    val startTime = System.nanoTime()
    val path = router.findShortestPath(origin, destination)
    return RoutingResult(
        path,
        (System.nanoTime() - startTime) / NANOS_TO_MILLIS,
        router.visitedWarehouseCount,
        calculateTotalDistance(path)
    )
}

private fun compareRoutingAlgorithms(
    repositories: RepositoryProvider,
    graph: List<Warehouse>
) {
    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return

    val bfsResult = runStandardBfs(repositories, origin, destination)
    val bidirectionalResult = runBidirectionalBfs(repositories, origin, destination)
    printComparisonReport(origin, destination, bfsResult, bidirectionalResult)
}

private fun printComparisonReport(
    origin: Warehouse,
    destination: Warehouse,
    bfsResult: RoutingResult,
    bidirectionalResult: RoutingResult
) {
    println("\n[ALGORITHM EFFICIENCY BENCHMARK]")
    println("------------------------------------------------------------")
    println(" Route: ${origin.id} -> ${destination.id}")
    println("------------------------------------------------------------")

    println("%-20s | %-6s | %-12s | %-10s".format("Algorithm", "Hops", "Evaluated", "Time (ms)"))
    println("----------------------------------------------------------")
    printRouterReport("Standard BFS", bfsResult)
    printRouterReport("Bidirectional BFS", bidirectionalResult)
    println("------------------------------------------------------------")
    printEfficiencyComparison(bfsResult.evaluatedWarehouses, bidirectionalResult.evaluatedWarehouses)
    println("============================================================")
}

private fun printRouterReport(
    name: String,
    result: RoutingResult
) {

    val hops =
        if (result.path != null) (result.path.size - 1).toString()
        else "N/A"
    val evaluated = result.evaluatedWarehouses.toString()
    val time = "%.4f".format(Locale.US, result.executionTime)

    println("%-20s | %-6s | %-12s | %-10s".format(name, hops, evaluated, time))
}

private fun printEfficiencyComparison(
    bfsEvaluated: Int,
    bidirectionalEvaluated: Int
) {
    if (bfsEvaluated == 0) return
    val improvement = (bfsEvaluated - bidirectionalEvaluated) * PERCENTAGE_MULTIPLIER / bfsEvaluated
    println(" RESULT: Bidirectional BFS evaluated %.2f%% fewer nodes!".format(Locale.US, improvement))
}
