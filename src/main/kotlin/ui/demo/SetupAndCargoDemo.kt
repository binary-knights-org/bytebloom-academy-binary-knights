package ui.demo

import domain.algorithm.sorting.CargoWeightQuickSorter
import domain.algorithm.sorting.ImportanceSelectionSorter
import domain.model.Package
import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

internal const val TOP_SHIPMENTS_LIMIT = 3
private const val QUEUE_DISPLAY_LIMIT = 5

internal suspend fun printParsingReport(
    vehicleRepository: VehicleRepository,
    warehouseRepository: WarehouseRepository,
    packageRepository: PackageRepository,
    routeRepository: RouteRepository
) {
    println("\n--- DATA PARSING REPORT ---")
    println("  Fleet      : ${vehicleRepository.getAll().size} records parsed.")
    println("  Packages   : ${packageRepository.getAll().size} records parsed.")
    println("  Routes     : ${routeRepository.getAll().size} records parsed.")
    println("  Warehouses : ${warehouseRepository.getAll().size} records parsed.")
}

internal suspend fun buildDomainGraph(
    warehouseRepository: WarehouseRepository
): List<Warehouse> {
    val graph = warehouseRepository.getAll()
    printGraphSummary(graph)
    return graph
}

private fun printGraphSummary(warehouses: List<Warehouse>) {
    println("\n--- DOMAIN GRAPH SUMMARY ---")
    println("  Total Connected Hubs: ${warehouses.size}")
    val firstHub = warehouses.firstOrNull()
    if (firstHub != null) {
        println("  Sample Hub: ${firstHub.id} (${firstHub.name}) | Zone: ${firstHub.regionalZone}")
        println("    -> Stationed Vehicles : ${firstHub.stationedVehicles.size}")
        println("    -> Cargo Queue        : ${firstHub.cargoQueue.size}")
        println("    -> Outgoing Routes    : ${firstHub.outgoingRoutes.size}")
    }
}

internal suspend fun runCargoDemos(
    packageRepository: PackageRepository,
    warehouses: List<Warehouse>
) {
    val importanceSorter = ImportanceSelectionSorter()
    val sortedPackages = importanceSorter.sortPackagesByImportance(packageRepository.getAll())
    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)
    printSortedCargoQueueForFirstWarehouse(warehouses)
}

private fun printTopShipments(packages: List<Package>, limit: Int) {
    println("\n--- TOP $limit PRIORITY SHIPMENTS ---")
    packages.take(limit).forEachIndexed { index, pkg ->
        println("  ${index + 1}. [${pkg.id}] To: ${pkg.destinationHub.id} " +
                "| ${pkg.weight} kg | Priority: ${pkg.priority}")
    }
}

private fun printSortedCargoQueueForFirstWarehouse(warehouses: List<Warehouse>) {
    val warehouse = warehouses.firstOrNull() ?: return

    val mutableCargoQueue = warehouse.cargoQueue.toMutableList()
    val cargoSorter = CargoWeightQuickSorter()
    cargoSorter.sortDescendingByWeight(mutableCargoQueue)

    println("\n--- SORTED CARGO QUEUE (DESCENDING BY WEIGHT) ---")
    println("  Warehouse: ${warehouse.id} (${warehouse.name})")
    mutableCargoQueue.take(QUEUE_DISPLAY_LIMIT).forEach { pkg ->
        println("    [${pkg.id}] -> ${pkg.weight} kg")
    }
    if (mutableCargoQueue.size > QUEUE_DISPLAY_LIMIT) {
        println("    ... and ${mutableCargoQueue.size - QUEUE_DISPLAY_LIMIT} more.")
    }
}
