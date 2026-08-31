package ui

import java.util.Locale
import domain.model.Package
import domain.model.Vehicle
import domain.ring.DeterministicHashingEngine
import domain.ring.breakdown.BreakdownSimulationLogic
import domain.ring.breakdown.VerificationReport
import domain.usecase.AnalyzeTreePerformanceUseCase

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

fun printTreePerformanceAnalysis(
    analyzeTreePerformanceUseCase: AnalyzeTreePerformanceUseCase,
    count: Int = 1000
) {
    println("[The Balanced Index Simulator]".uppercase())
    println("-".repeat(60))

    val perfAnalysis = analyzeTreePerformanceUseCase(count)

    println("Generated ${perfAnalysis.totalCount} sequential tracking IDs")
    println("Unbalanced BST Search Steps:")
    println("  - Max steps (Worst Case):  ${perfAnalysis.unbalancedMaxSteps} (Degrades to O(N) linear time)")
    println("  - Total steps ($count keys): ${perfAnalysis.unbalancedTotalSteps}")
    println("  - Average steps per search: ${"%.2f".format(Locale.US, perfAnalysis.unbalancedAvgSteps)}")

    println("Balanced AVL Tree Search Steps:")
    println("  - Max steps (Worst Case):  ${perfAnalysis.balancedMaxSteps} (Maintains O(log N) logarithmic time)")
    println("  - Total steps ($count keys): ${perfAnalysis.balancedTotalSteps}")
    println("  - Average steps per search: ${"%.2f".format(Locale.US, perfAnalysis.balancedAvgSteps)}")
    println("============================================================")
}
