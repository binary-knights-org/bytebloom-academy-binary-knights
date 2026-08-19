package ui

import data.dataholder.PackageRaw
import data.repository.CsvPackageRepository
import data.repository.CsvRouteRepository
import data.repository.CsvVehicleRepository
import data.repository.CsvWarehouseRepository
import domain.algorithm.sorting.sortPackagesByImportance
import domain.builder.DomainGraphBuilder
import domain.builder.RepositoryProvider
import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

internal const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
internal const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
internal const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
internal const val VEHICLES_FILE_PATH = "src/main/resources/fleet.csv"
internal const val TOP_SHIPMENTS_LIMIT = 3

internal fun initializeRepositories(): RepositoryProvider {
    val repositories = RepositoryProvider(
        vehicleRepository = CsvVehicleRepository(VEHICLES_FILE_PATH),
        packageRepository = CsvPackageRepository(PACKAGE_FILE_PATH),
        routeRepository = CsvRouteRepository(ROUTES_FILE_PATH),
        warehouseRepository = CsvWarehouseRepository(WAREHOUSES_FILE_PATH)
    )
    printParsingReport(repositories)
    return repositories
}

private fun printParsingReport(repositories: RepositoryProvider) {
    println("\n--- Data Parsing Report ---")
    println(" Successfully parsed Fleet: ${repositories.vehicleRepository.getAllVehicles().size} vehicle records.")
    println(" Successfully parsed Packages: ${repositories.packageRepository.getAllPackages().size} records.")
    println(" Successfully parsed Routes: ${repositories.routeRepository.getAllRoutes().size} records.")
    println(" Successfully parsed Warehouses: ${repositories.warehouseRepository.getAllWarehouses().size} records.")
}

internal fun buildDomainGraph(repositories: RepositoryProvider): List<Warehouse> {
    val graph = DomainGraphBuilder(repositories).buildGraph()
    printGraphSummary(graph)
    return graph
}

private fun printGraphSummary(warehouses: List<Warehouse>) {
    println("\n--- Domain Graph Summary ---")
    println("Total Warehouses Built: ${warehouses.size}")

    for (warehouse in warehouses) {
        println("\nHub: ${warehouse.id} (${warehouse.name}) | Zone: ${warehouse.regionalZone}")
        println("  Stationed Vehicles: ${warehouse.stationedVehicles.size}")
        println("  Cargo Queue: ${warehouse.cargoQueue.size}")
        println("  Outgoing Routes: ${warehouse.outgoingRoutes.size}")
    }
}

internal fun runCargoDemos(repositories: RepositoryProvider, graph: List<Warehouse>) {
    val sortedPackages = sortPackagesByImportance(repositories.packageRepository.getAllPackages())
    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)
    printSortedCargoQueueForFirstWarehouse(graph)
}

private fun printTopShipments(packages: List<PackageRaw>, limit: Int) {
    println("\n--- Executing Manual Package Sorting ---")
    println("\n--- Top $limit Priority Shipments ---")

    packages.take(limit).forEachIndexed { index, pkg ->
        println(
            "package = ${index + 1} , id = ${pkg.packageId} , destinationHub = ${pkg.destinationHubId} , " +
                    "weight = ${pkg.weight} kg , priority = ${pkg.priority}"
        )
    }
}

private fun printSortedCargoQueueForFirstWarehouse(warehouses: List<Warehouse>) {
    val warehouse = warehouses.firstOrNull()
    if (warehouse == null) {
        println("\nNo warehouse available to sort cargo queue.")
        return
    }

    warehouse.sortCargoQueueByWeightDescending()
    println("\n--- Sorted Cargo Queue (Warehouse: ${warehouse.id}) ---")
    warehouse.cargoQueue.forEach { pkg ->
        println("id = ${pkg.id}, weight = ${pkg.weight} kg")
    }
}
