package ui

import domain.model.Package
import domain.model.Vehicle
import domain.ring.DeterministicHashingEngine
import domain.ring.breakdown.BreakdownSimulationLogic
import domain.ring.breakdown.VerificationReport

internal fun runBreakdownSimulationDemo() {
    val simulationLogic = BreakdownSimulationLogic()
    val result = simulationLogic.runSimulation()

    printAssignments(result.before, "--- Initial Assignment BEFORE Breakdown ---")
    println(
        "\nRemoving broken vehicle at slot ${result.breakdownEvent.slot} " +
                "(${result.breakdownEvent.brokenVehicle.id})..."
    )
    printAssignments(result.after, "--- Re-routing Assignment AFTER Breakdown ---")

    val report = simulationLogic.createReport(result)
    printVerificationReport(report)
}

private fun printAssignments(assignments: Map<Package, Vehicle>, title: String) {
    println("\n$title")
    assignments.forEach { (pkg, vehicle) ->
        val slot = DeterministicHashingEngine.calculateSlot(pkg)
        println("  - ${pkg.id} (Slot %02d) -> Assigned to ${vehicle.id}".format(slot))
    }
}

private fun printVerificationReport(report: VerificationReport) {
    println("\n--- Verification Report ---")
    println("Packages migrated from broken vehicle: ${report.migratedPackageIds.size}")

    if (report.migratedPackageIds.isNotEmpty()) {
        println(
            "SUCCESS: The following packages successfully moved from " +
                    "${report.brokenVehicleId} to ${report.fallbackVehicleId}:"
        )
        println(" -> ${report.migratedPackageIds.joinToString(", ")}")
    }
}
