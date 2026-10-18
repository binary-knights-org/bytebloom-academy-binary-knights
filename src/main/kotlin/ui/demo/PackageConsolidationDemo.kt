package ui

import domain.usecase.vehicle.AssignPackagesToVehicleUseCase

private const val DISPLAY_LIMIT = 10

internal suspend fun runPackageConsolidationDemo(
    assignPackagesToVehicleUseCase: AssignPackagesToVehicleUseCase
) {
    val assignments = assignPackagesToVehicleUseCase()

    println("\n--- PACKAGE CONSOLIDATION ---")

    if (assignments.isEmpty()) {
        println("  No packages were assigned to available vehicles.")
        return
    }

    println("  Packages assigned to ${assignments.size} vehicles.")
    assignments.take(DISPLAY_LIMIT).forEachIndexed { index, assignment ->
        val packageIds = assignment.packages.joinToString { it.id }
        println("  ${index + 1}. Vehicle: ${assignment.vehicle.id} -> Packages: [$packageIds]")
    }

    if (assignments.size > DISPLAY_LIMIT) {
        println("  ... and ${assignments.size - DISPLAY_LIMIT} more.")
    }
}
