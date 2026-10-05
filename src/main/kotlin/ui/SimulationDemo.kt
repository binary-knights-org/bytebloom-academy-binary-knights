package ui

import domain.algorithm.ring.DeterministicHashingEngine
import domain.algorithm.ring.breakdown.BreakdownSimulationLogic
import domain.algorithm.ring.breakdown.VerificationReport
import domain.command.CommandInvoker
import domain.command.DispatchVehicleCommand
import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import domain.usecase.analytics.AnalyzeTreePerformanceUseCase
import domain.usecase.analytics.CalculateNetworkResilienceScoreUseCase
import domain.usecase.vehicle.DispatchVehicleUseCase
import java.util.Locale

private const val DISPLAY_LIMIT = 3
private const val MIGRATED_DISPLAY_LIMIT = 5
private const val DEFAULT_PACKAGE_COUNT = 1000

internal fun runBreakdownSimulationDemo() {
    val simulationLogic = BreakdownSimulationLogic()
    val result = simulationLogic.runSimulation()

    println("\n--- CONSISTENT HASHING & FAILOVER SIMULATION ---")
    printAssignments(result.before, "Initial Assignment (System Healthy)")

    println("\n  ALERT: Vehicle ${result.breakdownEvent.brokenVehicle.id} (Slot ${result.breakdownEvent.slot}) went offline!")
    println("  Initiating failover protocol...\n")

    printAssignments(result.after, "Re-routing Assignment (After Breakdown)")

    val report = simulationLogic.createReport(result)
    printVerificationReport(report)
}

private fun printAssignments(assignments: Map<Package, Vehicle>, title: String) {
    println("  $title:")
    assignments.entries.take(DISPLAY_LIMIT).forEach { (pkg, vehicle) ->
        val slot = DeterministicHashingEngine.calculateSlot(pkg)
        println("    [${pkg.id}] -> Slot %02d -> Assigned to: ${vehicle.id}".format(slot))
    }
    if (assignments.size > DISPLAY_LIMIT) {
        println("    ... and ${assignments.size - DISPLAY_LIMIT} more packages.")
    }
}

private fun printVerificationReport(report: VerificationReport) {
    println("\n--- FAILOVER VERIFICATION REPORT ---")
    println("  Packages Migrated: ${report.migratedPackageIds.size}")
    if (report.migratedPackageIds.isNotEmpty()) {
        println("  Packages safely moved from ${report.brokenVehicleId} to fallback ${report.fallbackVehicleId}.")
        val movedList = report.migratedPackageIds.take(MIGRATED_DISPLAY_LIMIT).joinToString(", ")
        val extra = if (report.migratedPackageIds.size > MIGRATED_DISPLAY_LIMIT) "..." else ""
        println("  Moved IDs: $movedList$extra")
    }
}

fun printTreePerformanceAnalysis(
    analyzeTreePerformanceUseCase: AnalyzeTreePerformanceUseCase, count: Int = 1000
) {
    println("\n--- INDEX TREE PERFORMANCE ANALYSIS ---")

    val perfAnalysis = analyzeTreePerformanceUseCase(count)

    println("  Generated $count tracking IDs:")
    println("  Unbalanced BST Search Steps:")
    println("    - Max steps (Worst Case) : ${perfAnalysis.unbalancedMaxSteps}")
    println("    - Total steps            : ${perfAnalysis.unbalancedTotalSteps}")
    println("    - Average steps          : %.2f".format(Locale.US, perfAnalysis.unbalancedAvgSteps))

    println("  Balanced AVL Tree Search Steps:")
    println("    - Max steps (Worst Case) : ${perfAnalysis.balancedMaxSteps}")
    println("    - Total steps            : ${perfAnalysis.balancedTotalSteps}")
    println("    - Average steps          : %.2f".format(Locale.US, perfAnalysis.balancedAvgSteps))
}

suspend fun printCommandPatternTest(
    dispatchVehicleUseCase: DispatchVehicleUseCase, firstWarehouse: Warehouse, firstVehicle: Vehicle
) {
    println("\n--- COMMAND PATTERN DISPATCH TEST ---")

    val commandInvoker = CommandInvoker()
    val secondVehicle = firstWarehouse.stationedVehicles.getOrNull(1)
    val thirdVehicle = firstWarehouse.stationedVehicles.getOrNull(2)

    if (secondVehicle == null || thirdVehicle == null) {
        println("  Multi-level test requires at least 3 vehicles.")
        return
    }

    val (dispatch1, dispatch2, dispatch3) = createDispatchCommands(
        dispatchVehicleUseCase, firstWarehouse, firstVehicle, secondVehicle, thirdVehicle
    )

    printCommandExecution(commandInvoker, dispatch1, firstWarehouse, firstVehicle, "Command 1")
    printCommandExecution(commandInvoker, dispatch2, firstWarehouse, secondVehicle, "Command 2")
    printUndo(commandInvoker, firstWarehouse, secondVehicle, "Undo Command 2")
    printUndo(commandInvoker, firstWarehouse, firstVehicle, "Undo Command 1")
    printRedo(commandInvoker, firstWarehouse, firstVehicle, "Redo Command 1")
    printRedo(commandInvoker, firstWarehouse, secondVehicle, "Redo Command 2")
    printHistoryClearance(commandInvoker, dispatch3, firstWarehouse, thirdVehicle)
}

private fun createDispatchCommands(
    useCase: DispatchVehicleUseCase, warehouse: Warehouse, v1: Vehicle, v2: Vehicle, v3: Vehicle
): Triple<DispatchVehicleCommand, DispatchVehicleCommand, DispatchVehicleCommand> {
    return Triple(
        DispatchVehicleCommand(useCase, v1, warehouse),
        DispatchVehicleCommand(useCase, v2, warehouse),
        DispatchVehicleCommand(useCase, v3, warehouse)
    )
}

private suspend fun printCommandExecution(
    commandInvoker: CommandInvoker,
    command: DispatchVehicleCommand,
    warehouse: Warehouse,
    vehicle: Vehicle,
    title: String
) {
    val executed = commandInvoker.executeCommand(command)
    println("  $title -> Executed: $executed | Queue: ${warehouse.cargoQueue.size} | Undo Stack: ${commandInvoker.undoHistorySize} | Redo Stack: ${commandInvoker.redoHistorySize}")
}

private suspend fun printUndo(
    commandInvoker: CommandInvoker, warehouse: Warehouse, vehicle: Vehicle, title: String
) {
    val undone = commandInvoker.undo()
    println("  $title -> Undone: $undone | Queue: ${warehouse.cargoQueue.size} | Undo Stack: ${commandInvoker.undoHistorySize} | Redo Stack: ${commandInvoker.redoHistorySize}")
}

private suspend fun printRedo(
    commandInvoker: CommandInvoker, warehouse: Warehouse, vehicle: Vehicle, title: String
) {
    val redone = commandInvoker.redo()
    println("  $title -> Redone: $redone | Queue: ${warehouse.cargoQueue.size} | Undo Stack: ${commandInvoker.undoHistorySize} | Redo Stack: ${commandInvoker.redoHistorySize}")
}

private suspend fun printHistoryClearance(
    commandInvoker: CommandInvoker, command: DispatchVehicleCommand, warehouse: Warehouse, vehicle: Vehicle
) {
    commandInvoker.undo()
    val executed = commandInvoker.executeCommand(command)
    println("  New Command Execution -> Executed: $executed | Redo Cleared: ${commandInvoker.redoHistorySize == 0}")
}

suspend fun runSimulationDemos(
    vehicleRepository: VehicleRepository, warehouseRepository: WarehouseRepository, warehouses: List<Warehouse>
) {
    printTreePerformanceAnalysis(AnalyzeTreePerformanceUseCase(), DEFAULT_PACKAGE_COUNT)
    printCommandPatternTest(
        dispatchVehicleUseCase = DispatchVehicleUseCase(vehicleRepository, warehouseRepository),
        firstWarehouse = warehouses.first(),
        firstVehicle = warehouses.first().stationedVehicles.first()
    )
    printNetworkResilienceAnalysis(CalculateNetworkResilienceScoreUseCase(), warehouses)
}

private fun printNetworkResilienceAnalysis(
    calculateNetworkResilienceScoreUseCase: CalculateNetworkResilienceScoreUseCase, graph: List<Warehouse>
) {
    println("\n--- NETWORK RESILIENCE ANALYSIS ---")
    val resilienceScore = calculateNetworkResilienceScoreUseCase(graph)
    println("  Network Resilience Score: %.2f".format(Locale.US, resilienceScore))
}
