package ui

import domain.model.Package
import domain.model.Vehicle
import domain.ring.DeterministicHashingEngine
import domain.ring.breakdown.BreakdownSimulationLogic
import domain.ring.breakdown.VerificationReport

private const val DISPLAY_LIMIT = 3
private const val MIGRATED_DISPLAY_LIMIT = 5

internal fun runBreakdownSimulationDemo() {
    val simulationLogic = BreakdownSimulationLogic()
    val result = simulationLogic.runSimulation()

    println("\n[CONSISTENT HASHING & FAILOVER SIMULATION]")
    println("============================================================")
    printAssignments(result.before, "INITIAL ASSIGNMENT (SYSTEM HEALTHY)")

    println(
        "\n ALERT: Vehicle ${result.breakdownEvent.brokenVehicle.id} " +
                "(Slot ${result.breakdownEvent.slot}) went offline!"
    )
    println("INITIATING FAILOVER PROTOCOL...\n")

    printAssignments(result.after, "RE-ROUTING ASSIGNMENT (AFTER BREAKDOWN)")

    val report = simulationLogic.createReport(result)
    printVerificationReport(report)
}

private fun printAssignments(assignments: Map<Package, Vehicle>, title: String) {
    println(title)
    println("------------------------------------------------------------")
    assignments.entries.take(DISPLAY_LIMIT).forEach { (pkg, vehicle) ->
        val slot = DeterministicHashingEngine.calculateSlot(pkg)
        println("   [${pkg.id}] -> Slot %02d -> Assigned to: ${vehicle.id}".format(slot))
    }
    if (assignments.size > DISPLAY_LIMIT)
        println("   ... and ${assignments.size - DISPLAY_LIMIT} more packages.")
}

private fun printVerificationReport(report: VerificationReport) {
    println("\n[FAILOVER VERIFICATION REPORT]")
    println("------------------------------------------------------------")
    println(" Packages Migrated: ${report.migratedPackageIds.size}")
    if (report.migratedPackageIds.isNotEmpty()) {
        println(
            " SUCCESS: Packages safely moved from " +
                    "${report.brokenVehicleId} to fallback ${report.fallbackVehicleId}."
        )
        println(
            "    Moved IDs: ${
                report.migratedPackageIds.take(MIGRATED_DISPLAY_LIMIT).joinToString(", ")
            }${if (report.migratedPackageIds.size > MIGRATED_DISPLAY_LIMIT) "..." else ""}"
        )
    }
    println("============================================================")
}
