package ui.demo

import domain.algorithm.pathfinding.CachingShortestPathRouter
import domain.algorithm.pathfinding.LruRouteCache
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import java.lang.management.ManagementFactory

private const val BENCHMARK_REQUEST_COUNT = 1_000
private const val CPU_BENCHMARK_REQUEST_COUNT = 100_000
private const val WARM_UP_REQUEST_COUNT = 100

private val threadCpuBean = ManagementFactory.getThreadMXBean()

internal suspend fun runLruRouteCacheBenchmarkDemo(
    warehouseRepository: WarehouseRepository,
    warehouses: List<Warehouse>
) {
    println("\n--- LRU ROUTE CACHE BENCHMARK ---")

    val route = findBenchmarkRoute(warehouseRepository, warehouses)

    if (route == null) {
        println("  Benchmark skipped.")
    } else {
        printRouteDetails(route)

        val results = measureBenchmarkResults(warehouseRepository, route)

        printBenchmarkResults(results)
    }
}

private suspend fun findBenchmarkRoute(
    warehouseRepository: WarehouseRepository,
    warehouses: List<Warehouse>
): BenchmarkRoute? {
    val origin = warehouses.firstOrNull()
    val destination = warehouses.lastOrNull()

    if (origin == null || destination == null || origin.id == destination.id) {
        println("  Benchmark requires two different warehouses.")
        return null
    }

    val expectedPath = OptimalTransitRouter(warehouseRepository)
        .findShortestPath(origin, destination)

    return expectedPath?.let {
        BenchmarkRoute(origin, destination, it)
    } ?: run {
        println("  No route found.")
        null
    }
}

private fun printRouteDetails(route: BenchmarkRoute) {
    println("  Route: ${route.origin.id} -> ${route.destination.id}")
    println("  Requests: $BENCHMARK_REQUEST_COUNT")
    println("  Path hops: ${route.expectedPath.size - 1}")
}

private suspend fun measureBenchmarkResults(
    warehouseRepository: WarehouseRepository,
    route: BenchmarkRoute
): BenchmarkResults {
    val regularRouter = OptimalTransitRouter(warehouseRepository)

    val regularExecutionTime = measureExecutionTime {
        repeat(BENCHMARK_REQUEST_COUNT) {
            regularRouter.findShortestPath(route.origin, route.destination)
        }
    }

    val cachingRouter = CachingShortestPathRouter(
        dijkstraRouter = OptimalTransitRouter(warehouseRepository),
        cache = LruRouteCache()
    )

    val cachingExecutionTime = measureExecutionTime {
        repeat(BENCHMARK_REQUEST_COUNT) {
            cachingRouter.findShortestPath(route.origin, route.destination)
        }
    }

    val timeSaved = regularExecutionTime - cachingExecutionTime

    val improvementPercentage =
        if (regularExecutionTime > 0.0) {
            (timeSaved / regularExecutionTime) * PERCENTAGE_MULTIPLIER
        } else {
            0.0
        }

    val regularCpuRouter = OptimalTransitRouter(warehouseRepository)

    val cpuCachingRouter = CachingShortestPathRouter(
        dijkstraRouter = OptimalTransitRouter(warehouseRepository),
        cache = LruRouteCache()
    )

    repeat(WARM_UP_REQUEST_COUNT) {
        regularCpuRouter.findShortestPath(route.origin, route.destination)
        cpuCachingRouter.findShortestPath(route.origin, route.destination)
    }

    val regularCpuTime = measureCurrentThreadCpuTime {
        repeat(CPU_BENCHMARK_REQUEST_COUNT) {
            regularCpuRouter.findShortestPath(route.origin, route.destination)
        }
    }

    val cachingCpuTime = measureCurrentThreadCpuTime {
        repeat(CPU_BENCHMARK_REQUEST_COUNT) {
            cpuCachingRouter.findShortestPath(route.origin, route.destination)
        }
    }

    val regularCpuTimeMs = regularCpuTime / NANOS_TO_MILLIS
    val cachingCpuTimeMs = cachingCpuTime / NANOS_TO_MILLIS

    return BenchmarkResults(
        regularExecutionTime = regularExecutionTime,
        cachingExecutionTime = cachingExecutionTime,
        timeSaved = timeSaved,
        improvementPercentage = improvementPercentage,
        regularCpuTime = regularCpuTimeMs,
        cachingCpuTime = cachingCpuTimeMs,
        cpuTimeSaved = regularCpuTimeMs - cachingCpuTimeMs,
        cacheHits = cachingRouter.cacheHits,
        cacheMisses = cachingRouter.cacheMisses,
        dijkstraCalls = cachingRouter.dijkstraCalls
    )
}

private suspend fun measureExecutionTime(
    block: suspend () -> Unit
): Double {
    val startTime = System.nanoTime()
    block()
    return (System.nanoTime() - startTime) / NANOS_TO_MILLIS
}

private suspend fun measureCurrentThreadCpuTime(
    block: suspend () -> Unit
): Double {
    if (!threadCpuBean.isCurrentThreadCpuTimeSupported) {
        throw UnsupportedOperationException(
            "Current thread CPU time measurement is not supported."
        )
    }

    if (!threadCpuBean.isThreadCpuTimeEnabled) {
        threadCpuBean.isThreadCpuTimeEnabled = true
    }

    val startTime = threadCpuBean.currentThreadCpuTime
    block()
    val endTime = threadCpuBean.currentThreadCpuTime

    return (endTime - startTime).coerceAtLeast(0L) / NANOS_TO_MILLIS
}

private fun printBenchmarkResults(results: BenchmarkResults) {
    println("\n  --- RESULTS ---")
    println("  Metric               |   Value")
    println("  -------------------------------------")

    printMetric("Execution Without Cache", results.regularExecutionTime)
    printMetric("Execution With LRU Cache", results.cachingExecutionTime)
    printMetric("Execution Time Saved", results.timeSaved)

    println("  Execution Improvement: ${results.improvementPercentage}%")

    println("\n  --- JVM THREAD CPU TIME ---")
    println("  CPU Benchmark Requests: $CPU_BENCHMARK_REQUEST_COUNT")
    printMetric("CPU Without Cache", results.regularCpuTime)
    printMetric("CPU With LRU Cache", results.cachingCpuTime)
    printMetric("CPU Time Saved", results.cpuTimeSaved)

    println("\n  --- CACHE STATISTICS ---")
    println("  Cache Hits:       ${results.cacheHits}")
    println("  Cache Misses:     ${results.cacheMisses}")
    println("  Dijkstra Calls:   ${results.dijkstraCalls}")
    println("  Total Requests:   $BENCHMARK_REQUEST_COUNT")
}

private fun printMetric(label: String, value: Double) {
    println("  $label: $value ms")
}

private data class BenchmarkRoute(
    val origin: Warehouse,
    val destination: Warehouse,
    val expectedPath: List<Warehouse>
)

private data class BenchmarkResults(
    val regularExecutionTime: Double,
    val cachingExecutionTime: Double,
    val timeSaved: Double,
    val improvementPercentage: Double,
    val regularCpuTime: Double,
    val cachingCpuTime: Double,
    val cpuTimeSaved: Double,
    val cacheHits: Int,
    val cacheMisses: Int,
    val dijkstraCalls: Int
)
