package ui

import java.util.Locale
import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.algorithm.pathfinding.ShortestPathRouter
import domain.model.Warehouse

internal const val NANOS_TO_MILLIS = 1_000_000.0
internal const val PERCENTAGE_MULTIPLIER = 100.0

internal data class RoutingResult(
    val path: List<Warehouse>?,
    val executionTime: Double,
    val evaluatedWarehouses: Int,
    val totalDistanceKm: Double
)

internal fun runRoutingAndComparisonDemos(graph: List<Warehouse>) {
    printRouteDemo(graph, LeastHopRouter(), "Least-Hop Router Demo (BFS)")
    printRouteDemo(graph, BidirectionalBfsRouter(), "Bidirectional Transit Router Demo")
    printRouteDemo(graph, OptimalTransitRouter(), "Optimal Transit Router Demo (Dijkstra)")

    compareRoutingAlgorithms(graph)
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

private fun printRouteDemo(graph: List<Warehouse>, router: ShortestPathRouter, label: String) {
    println("\n--- $label ---")
    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return
    println("Finding path from ${origin.id} to ${destination.id}...")

    val path = router.findShortestPath(origin, destination)
    if (path == null) {
        println("No path found: ${destination.id} is not reachable from ${origin.id}.")
    } else {
        val distance = calculateTotalDistance(path)
        println(
            "Path (${path.size - 1} hop(s), %.2f km): ${path.joinToString(" -> ") { it.id }}"
                .format(Locale.US, distance)
        )
    }
}

private fun runStandardBfs(origin: Warehouse, destination: Warehouse): RoutingResult {
    val router = LeastHopRouter()
    val startTime = System.nanoTime()
    val path = router.findShortestPath(origin, destination)
    return RoutingResult(
        path = path,
        executionTime = (System.nanoTime() - startTime) / NANOS_TO_MILLIS,
        evaluatedWarehouses = router.visitedWarehouseCount,
        totalDistanceKm = calculateTotalDistance(path)
    )
}

private fun runBidirectionalBfs(origin: Warehouse, destination: Warehouse): RoutingResult {
    val router = BidirectionalBfsRouter()
    val startTime = System.nanoTime()
    val path = router.findShortestPath(origin, destination)
    return RoutingResult(
        path = path,
        executionTime = (System.nanoTime() - startTime) / NANOS_TO_MILLIS,
        evaluatedWarehouses = router.visitedWarehouseCount,
        totalDistanceKm = calculateTotalDistance(path)
    )
}

private fun compareRoutingAlgorithms(graph: List<Warehouse>) {
    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return

    val bfsResult = runStandardBfs(origin, destination)
    val bidirectionalResult = runBidirectionalBfs(origin, destination)

    printComparisonReport(origin, destination, bfsResult, bidirectionalResult)
}

private fun printComparisonReport(
    origin: Warehouse, destination: Warehouse, bfsResult: RoutingResult, bidirectionalResult: RoutingResult
) {
    println("\n============================================================")
    println("              ROUTING ALGORITHM COMPARISON")
    println("============================================================")
    println("Origin      : ${origin.id}\nDestination : ${destination.id}")

    printRouterReport("Standard BFS (Least-Hop Router)", bfsResult)
    printRouterReport("Bidirectional BFS", bidirectionalResult)
    printEfficiencyComparison(bfsResult.evaluatedWarehouses, bidirectionalResult.evaluatedWarehouses)
}

private fun printRouterReport(name: String, result: RoutingResult) {
    println("\n------------------------------------------------------------\n$name\n------------------------------------------------------------")
    if (result.path == null) {
        println("No path found.")
        return
    }
    println("Path       : ${result.path.joinToString(" -> ") { it.id }}")
    println("Hops       : ${result.path.size - 1}")
    println("Distance   : %.2f km".format(Locale.US, result.totalDistanceKm))
    println("Evaluated  : ${result.evaluatedWarehouses}")
    println("Time       : %.4f ms".format(Locale.US, result.executionTime))
}

private fun printEfficiencyComparison(bfsEvaluated: Int, bidirectionalEvaluated: Int) {
    if (bfsEvaluated == 0) return
    val improvement = (bfsEvaluated - bidirectionalEvaluated) * PERCENTAGE_MULTIPLIER / bfsEvaluated
    println("\n------------------------------------------------------------\nEfficiency\n------------------------------------------------------------")
    println("Bidirectional BFS evaluated %.2f%% fewer warehouses.".format(Locale.US, improvement))
}
