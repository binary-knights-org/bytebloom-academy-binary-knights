package ui

import domain.usecase.AssignPackagesToVehicleUseCase
import domain.usecase.FindPackagesForConsolidationUseCase
import domain.usecase.FindSuitableVehicleUseCase

private const val DISPLAY_LIMIT = 10

internal fun runPackageConsolidationDemo(
    findPackagesForConsolidationUseCase: FindPackagesForConsolidationUseCase,
    findSuitableVehicleUseCase: FindSuitableVehicleUseCase,
    assignPackagesToVehicleUseCase: AssignPackagesToVehicleUseCase
) {
    val packageGroups = findPackagesForConsolidationUseCase()

    println("\n[PACKAGE CONSOLIDATION]")
    println("------------------------------------------------------------")

    val assignments = packageGroups.mapNotNull { packages ->
        val vehicle = findSuitableVehicleUseCase(packages)

        vehicle?.let { assignPackagesToVehicleUseCase(packages, it) }
    }

    if (assignments.isEmpty()) {
        println(" No packages were assigned to available vehicles.")
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