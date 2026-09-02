package ui

import domain.usecase.RecommendPackageConsolidationUseCase

private const val DISPLAY_LIMIT = 15

internal fun runPackageConsolidationDemo(
    useCase: RecommendPackageConsolidationUseCase
) {
    val recommendations = useCase()
    println("\n[PACKAGE CONSOLIDATION RECOMMENDATIONS]")
    println("------------------------------------------------------------")
    if (recommendations.isEmpty()) {
        println(" No consolidation opportunities found.")
        println("------------------------------------------------------------")
        return
    }
    println(" Found ${recommendations.size} consolidation opportunities.")
    recommendations.take(DISPLAY_LIMIT).forEachIndexed { index, recommendation ->
        val remainingCapacity = recommendation.vehicle.maxCapacityKg -
                recommendation.vehicle.currentLoadKg - recommendation.totalWeight
        println(" ${index + 1}. ${recommendation.origin.id} -> ${recommendation.destination.id}")
        println("    Packages    : ${recommendation.packages.size}")
        println("    Total Weight: ${recommendation.totalWeight} kg")
        println("    Vehicle     : ${recommendation.vehicle.id}")
        println("    Remaining Capacity: $remainingCapacity kg")
        println()
    }
    if (recommendations.size > DISPLAY_LIMIT) {
        println(" ... and ${recommendations.size - DISPLAY_LIMIT} more.")
    }
    println("------------------------------------------------------------")
}
