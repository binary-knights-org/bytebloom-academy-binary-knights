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
    dispatchVehicleUseCase: DispatchVehicleUseCase,
    firstWarehouse: Warehouse,
    firstVehicle: Vehicle
) {
    println("\n[Time-Machine Dispatch Panel]".uppercase())
    println("============================================================")

    val commandInvoker = CommandInvoker()

    val secondVehicle = firstWarehouse.stationedVehicles.getOrNull(1)
    val thirdVehicle = firstWarehouse.stationedVehicles.getOrNull(2)

    if (secondVehicle == null || thirdVehicle == null) {
        println("  - Multi-level test requires at least 3 vehicles.")
        println("============================================================")
        return
    }

    val dispatchCommand1 = DispatchVehicleCommand(
        dispatchVehicleUseCase,
        firstVehicle,
        firstWarehouse
    )

    val dispatchCommand2 = DispatchVehicleCommand(
        dispatchVehicleUseCase,
        secondVehicle,
        firstWarehouse
    )

    val dispatchCommand3 = DispatchVehicleCommand(
        dispatchVehicleUseCase,
        thirdVehicle,
        firstWarehouse
    )

    println("== Command 1 ==")

    val executedFirst = commandInvoker.executeCommand(dispatchCommand1)

    println("Execution:")
    println("  - Success: $executedFirst")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${firstVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    println("\n== Command 2 ==")

    val executedSecond = commandInvoker.executeCommand(dispatchCommand2)

    println("Execution:")
    println("  - Success: $executedSecond")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${secondVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    println("\n== Undo Command 2 ==")

    val undoneSecond = commandInvoker.undo()

    println("Undo Operation:")
    println("  - Success: $undoneSecond")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${secondVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    println("\n== Undo Command 1 ==")

    val undoneFirst = commandInvoker.undo()

    println("Undo Operation:")
    println("  - Success: $undoneFirst")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${firstVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    println("\n== Redo Command 1 ==")

    val redoneFirst = commandInvoker.redo()

    println("Redo Operation:")
    println("  - Success: $redoneFirst")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${firstVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    println("\n== Redo Command 2 ==")

    val redoneSecond = commandInvoker.redo()

    println("Redo Operation:")
    println("  - Success: $redoneSecond")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${secondVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    println("\n== History Clearance ==")

    val undoneAgain = commandInvoker.undo()

    println("Undo Operation:")
    println("  - Success: $undoneAgain")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")

    val executedThird = commandInvoker.executeCommand(dispatchCommand3)

    println("New Command Execution:")
    println("  - Success: $executedThird")
    println("  - Queue size: ${firstWarehouse.cargoQueue.size}")
    println("  - Vehicle loaded cargo size: ${thirdVehicle.loadedCargo.size}")
    println("  - Undo stack size: ${commandInvoker.undoHistorySize}")
    println("  - Redo stack size: ${commandInvoker.redoHistorySize}")
    println("    Redo cleared: ${commandInvoker.redoHistorySize == 0}")

    println("============================================================")
}
