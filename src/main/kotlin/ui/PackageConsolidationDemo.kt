package ui

import domain.usecase.AssignPackagesToAvailableVehicleUseCase

private const val DISPLAY_LIMIT = 10

internal fun runPackageConsolidationDemo(
    useCase: AssignPackagesToAvailableVehicleUseCase
) {
    val assignments = useCase()

    println("\n[PACKAGE CONSOLIDATION]")
    println("------------------------------------------------------------")

    if (assignments.isEmpty()) {
        println(" No available vehicles found.")
        println("------------------------------------------------------------")
        return
    }
    println(" Packages assigned to ${assignments.size} vehicles.")

    assignments.take(DISPLAY_LIMIT).forEachIndexed { index, assignment ->
        println(" ${index + 1}. Vehicle: ${assignment.vehicle.id}")
        println("    Packages: ${assignment.packages.joinToString { it.id }}")
    }

    if (assignments.size > DISPLAY_LIMIT) {
        println(" ... and ${assignments.size - DISPLAY_LIMIT} more.")
    }
    println("------------------------------------------------------------")
}