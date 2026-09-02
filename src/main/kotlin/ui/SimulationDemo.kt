package ui

import domain.command.CommandInvoker
import domain.command.DispatchVehicleCommand
import java.util.Locale
import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.ring.DeterministicHashingEngine
import domain.ring.breakdown.BreakdownSimulationLogic
import domain.ring.breakdown.VerificationReport
import domain.usecase.AnalyzeTreePerformanceUseCase
import domain.usecase.CalculateNetworkResilienceScoreUseCase
import domain.usecase.DispatchVehicleUseCase

private const val DISPLAY_LIMIT = 3
private const val MIGRATED_DISPLAY_LIMIT = 5
private const val HIGH_RESILIENCE = 80.0
private const val MODERATE_RESILIENCE = 50.0

internal fun runBreakdownSimulationDemo() {
    val simulationLogic = BreakdownSimulationLogic()
    val result = simulationLogic.runSimulation()

    println("\n[CONSISTENT HASHING & FAILOVER SIMULATION]")
    println("============================================================")
    printAssignments(result.before, "INITIAL ASSIGNMENT (SYSTEM HEALTHY)")

    println(
        "\n ALERT: Vehicle ${result.breakdownEvent.brokenVehicle.id} "
                + "(Slot ${result.breakdownEvent.slot}) went offline!"
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
    if (assignments.size > DISPLAY_LIMIT) println("   ... and ${assignments.size - DISPLAY_LIMIT} more packages.")
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
    analyzeTreePerformanceUseCase: AnalyzeTreePerformanceUseCase, count: Int = 1000
) {
    println("\n[The Balanced Index Simulator]".uppercase())
    println("============================================================")

    val perfAnalysis = analyzeTreePerformanceUseCase(count)

    println("Generated ${perfAnalysis.totalCount} sequential tracking IDs")
    println("\nUnbalanced BST Search Steps:")
    println("  - Max steps (Worst Case):  ${perfAnalysis.unbalancedMaxSteps} (Degrades to O(N) linear time)")
    println("  - Total steps ($count keys): ${perfAnalysis.unbalancedTotalSteps}")
    println("  - Average steps per search: ${"%.2f".format(Locale.US, perfAnalysis.unbalancedAvgSteps)}")

    println("\n==================================================")

    println("Balanced AVL Tree Search Steps:")
    println("  - Max steps (Worst Case):  ${perfAnalysis.balancedMaxSteps} (Maintains O(log N) logarithmic time)")
    println("  - Total steps ($count keys): ${perfAnalysis.balancedTotalSteps}")
    println("  - Average steps per search: ${"%.2f".format(Locale.US, perfAnalysis.balancedAvgSteps)}")
    println("============================================================")
}

fun printCommandPatternTest(
    dispatchVehicleUseCase: DispatchVehicleUseCase, firstWarehouse: Warehouse, firstVehicle: Vehicle
) {
    println("\n[Command Pattern Dispatch Panel]".uppercase())
    println("============================================================")
    val commandInvoker = CommandInvoker()
    val queueCountBefore = firstWarehouse.cargoQueue.size
    val loadedCargoCountBefore = firstVehicle.loadedCargo.size

    val dispatchCommand = DispatchVehicleCommand(
        dispatchVehicleUseCase, firstVehicle, firstWarehouse
    )

    val executed = commandInvoker.executeCommand(dispatchCommand)

    println("Execution:")
    println("  - Success: $executed")
    println("  - Queue size after dispatch: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${firstVehicle.loadedCargo.size}")
    println("  - Command history size: ${commandInvoker.historySize}")

    println("\n==================================================")
    val undone = commandInvoker.undo()

    println("Undo Operation:")
    println("  - Success: $undone")
    println("  - Queue size after undo: ${firstWarehouse.cargoQueue.size}")
    println("    Restored: ${firstWarehouse.cargoQueue.size == queueCountBefore}")
    println("  - Vehicle loaded cargo size after undo: ${firstVehicle.loadedCargo.size}")
    println("    Restored: ${firstVehicle.loadedCargo.size == loadedCargoCountBefore}")
    println("  - Command history size after undo: ${commandInvoker.historySize}")

}

fun printNetworkResilienceAnalysis(
    calculateNetworkResilienceScoreUseCase: CalculateNetworkResilienceScoreUseCase,
    graph: List<Warehouse>
) {
    println("\n[Network Resilience Analysis]".uppercase())
    println("============================================================")

    val resilienceScore = calculateNetworkResilienceScoreUseCase(graph)

    println("Simulating Single-Point-of-Failure (SPOF) Outages...")
    println("  - Network Resilience Score : ${"%.2f".format(resilienceScore)}%")

    val statusMessage = when {
        resilienceScore >= HIGH_RESILIENCE -> "HIGH RESILIENCE (Network survives most single-node failures)"
        resilienceScore >= MODERATE_RESILIENCE -> "MODERATE RESILIENCE (Critical bottlenecks detected)"
        else -> "CRITICAL VULNERABILITY (High risk of network disconnection)"
    }

    println("  - System Status            : $statusMessage")
    println("============================================================")
}
