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

    val origin = warehouses.firstOrNull() ?: return
    val destination = warehouses.lastOrNull() ?: return

    if (origin.id == destination.id) {
        println("  Benchmark requires two different warehouses.")
        return
    }

    val validationRouter = OptimalTransitRouter(warehouseRepository)
    val expectedPath = validationRouter.findShortestPath(origin, destination)

    if (expectedPath == null) {
        println("  No route found. Benchmark skipped.")
        return
    }

    println("  Route: ${origin.id} -> ${destination.id}")
    println("  Requests: $BENCHMARK_REQUEST_COUNT")
    println("  Path hops: ${expectedPath.size - 1}")

    val regularRouter = OptimalTransitRouter(warehouseRepository)

    val regularStartTime = System.nanoTime()

    repeat(BENCHMARK_REQUEST_COUNT) {
        regularRouter.findShortestPath(origin, destination)
    }

    val regularExecutionTime =
        (System.nanoTime() - regularStartTime) / NANOS_TO_MILLIS

    val cachingRouter = CachingShortestPathRouter(
        dijkstraRouter = OptimalTransitRouter(warehouseRepository),
        cache = LruRouteCache()
    )

    val cachingStartTime = System.nanoTime()

    repeat(BENCHMARK_REQUEST_COUNT) {
        cachingRouter.findShortestPath(origin, destination)
    }

    val cachingExecutionTime =
        (System.nanoTime() - cachingStartTime) / NANOS_TO_MILLIS

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
        regularCpuRouter.findShortestPath(origin, destination)
        cpuCachingRouter.findShortestPath(origin, destination)
    }

    val regularCpuTime = measureCurrentThreadCpuTime {
        repeat(CPU_BENCHMARK_REQUEST_COUNT) {
            regularCpuRouter.findShortestPath(origin, destination)
        }
    }

    val cachingCpuTime = measureCurrentThreadCpuTime {
        repeat(CPU_BENCHMARK_REQUEST_COUNT) {
            cpuCachingRouter.findShortestPath(origin, destination)
        }
    }

    val regularCpuTimeMs = regularCpuTime / NANOS_TO_MILLIS
    val cachingCpuTimeMs = cachingCpuTime / NANOS_TO_MILLIS
    val cpuTimeSaved = regularCpuTimeMs - cachingCpuTimeMs

    val results = BenchmarkResults(
        regularExecutionTime = regularExecutionTime,
        cachingExecutionTime = cachingExecutionTime,
        timeSaved = timeSaved,
        improvementPercentage = improvementPercentage,
        regularCpuTime = regularCpuTimeMs,
        cachingCpuTime = cachingCpuTimeMs,
        cpuTimeSaved = cpuTimeSaved,
        cacheHits = cachingRouter.cacheHits,
        cacheMisses = cachingRouter.cacheMisses,
        dijkstraCalls = cachingRouter.dijkstraCalls
    )

    printBenchmarkResults(results)
}

private suspend fun measureCurrentThreadCpuTime(
    block: suspend () -> Unit
): Long {
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

    return (endTime - startTime).coerceAtLeast(0L)
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
