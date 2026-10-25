package ui.demo

import domain.algorithm.optimization.RouteDistanceCalculator
import domain.algorithm.optimization.TwoOptRouteOptimizer
import java.util.Locale


internal fun runTwoOptRoutingDemo() {
    val originalTour = DefaultTwoOptDemoData().createTour()
    val optimizer = TwoOptRouteOptimizer()
    val distanceCalculator = RouteDistanceCalculator()

    val originalDistance =
        distanceCalculator.calculateTotalDistanceKm(originalTour)

    val optimizedTour = optimizer.optimize(originalTour)

    val optimizedDistance =
        distanceCalculator.calculateTotalDistanceKm(optimizedTour)

    val distanceSaved = originalDistance - optimizedDistance
    val distanceReductionPercentage =
        calculatePercentageSaved(originalDistance, distanceSaved)

    printReport(
        originalDistance = originalDistance,
        optimizedDistance = optimizedDistance,
        distanceSaved = distanceSaved,
        distanceReductionPercentage = distanceReductionPercentage,
        optimizedTour = optimizedTour
    )
}

private fun calculatePercentageSaved(
    originalDistance: Double,
    distanceSaved: Double
): Double {
    if (originalDistance <= MINIMUM_DISTANCE_KM) {
        return MINIMUM_PERCENTAGE
    }

    return distanceSaved / originalDistance * PERCENTAGE_MULTIPLIER
}

private fun printReport(
    originalDistance: Double,
    optimizedDistance: Double,
    distanceSaved: Double,
    distanceReductionPercentage: Double,
    optimizedTour: List<domain.model.Warehouse>
) {
    println("\n--- 2-OPT LAST-MILE ROUTE OPTIMIZATION ---")
    println("Delivery stops: ${optimizedTour.size - TOUR_ENDPOINT_COUNT}")
    println("Original distance: %.2f km".format(Locale.US, originalDistance))
    println("Optimized distance: %.2f km".format(Locale.US, optimizedDistance))
    println("Distance saved: %.2f km".format(Locale.US, distanceSaved))
    println(
        "Estimated distance reduction: %.2f%%".format(
            Locale.US,
            distanceReductionPercentage
        )
    )
    println(
        "Estimated fuel saved: %.2f%%".format(
            Locale.US,
            distanceReductionPercentage
        )
    )
    println("Optimized tour:")
    println(optimizedTour.joinToString(" -> ") { it.name })
    println(
        "Note: Distances are geographic estimates. Fuel savings assume " +
                "constant fuel consumption per kilometer."
    )
}

private const val TOUR_ENDPOINT_COUNT = 2
private const val MINIMUM_DISTANCE_KM = 0.0
private const val MINIMUM_PERCENTAGE = 0.0
