package ui

import data.repository.CsvPackageRepository
import data.repository.CsvRouteRepository
import data.repository.CsvVehicleRepository
import data.repository.CsvWarehouseRepository
import domain.algorithm.sorting.sortPackagesByImportance
import domain.builder.DomainGraphBuilder
import domain.builder.RepositoryProvider
import domain.model.Package
import domain.model.Warehouse

internal const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
internal const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
internal const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
internal const val VEHICLES_FILE_PATH = "src/main/resources/fleet.csv"
internal const val TOP_SHIPMENTS_LIMIT = 3

private const val PAD_SMALL = 4
private const val PAD_MEDIUM = 5
private const val PAD_LARGE = 8
private const val QUEUE_DISPLAY_LIMIT = 5

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
    println("\n[DATA PARSING REPORT]")
    println("------------------------------------------------------------")
    println(
        " Fleet       : ${
            repositories.vehicleRepository.getAllVehicles().size.toString().padEnd(PAD_SMALL)
        } records parsed."
    )
    println(
        " Packages    : ${
            repositories.packageRepository.getAllPackages().size.toString().padEnd(PAD_SMALL)
        } records parsed."
    )
    println(
        " Routes      : ${
            repositories.routeRepository.getAllRoutes().size.toString().padEnd(PAD_SMALL)
        } records parsed."
    )
    println(
        " Warehouses  : ${
            repositories.warehouseRepository.getAllWarehouses().size.toString().padEnd(PAD_SMALL)
        } records parsed."
    )
    println("------------------------------------------------------------")
}

internal fun buildDomainGraph(repositories: RepositoryProvider): List<Warehouse> {
    val graph = DomainGraphBuilder(repositories).buildGraph()
    printGraphSummary(graph)
    return graph
}

private fun printGraphSummary(warehouses: List<Warehouse>) {
    println("\n[DOMAIN GRAPH SUMMARY]")
    println("------------------------------------------------------------")
    println("Total Connected Hubs: ${warehouses.size}")
    val firstHub = warehouses.firstOrNull()
    if (firstHub != null) {
        println("\nSample Hub: ${firstHub.id} (${firstHub.name}) | Zone: ${firstHub.regionalZone}")
        println("   -->  Stationed Vehicles : ${firstHub.stationedVehicles.size}")
        println("   -->  Cargo Queue        : ${firstHub.cargoQueue.size}")
        println("   -->  Outgoing Routes    : ${firstHub.outgoingRoutes.size}")
    }
    println("------------------------------------------------------------")
}

internal fun runCargoDemos(graph: List<Warehouse>) {
    val packages = graph.flatMap { it.cargoQueue }
    val sortedPackages = sortPackagesByImportance(packages)
    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)
    printSortedCargoQueueForFirstWarehouse(graph)
}

private fun printTopShipments(packages: List<Package>, limit: Int) {
    println("\n[TOP $limit PRIORITY SHIPMENTS]")
    println("------------------------------------------------------------")
    packages.take(limit).forEachIndexed { index, pkg ->
        val weightFormatted = "${pkg.weight} kg".padEnd(PAD_LARGE)
        println(
            " ${index + 1}. [${pkg.id}] To: ${pkg.destinationHub.id.padEnd(PAD_MEDIUM)}" +
                    " | $weightFormatted | ${pkg.priority}"
        )
    }
}

private fun printSortedCargoQueueForFirstWarehouse(warehouses: List<Warehouse>) {
    val warehouse = warehouses.firstOrNull() ?: return
    warehouse.sortCargoQueueByWeightDescending()

    println("\n[SORTED CARGO QUEUE - DESCENDING BY WEIGHT]")
    println("------------------------------------------------------------")
    println(" Warehouse: ${warehouse.id} (${warehouse.name})")
    warehouse.cargoQueue.take(PAD_MEDIUM).forEach { pkg ->
        println("   [${pkg.id}] -> ${pkg.weight} kg")
    }
    if (warehouse.cargoQueue.size > PAD_MEDIUM)
        println("   ... and ${warehouse.cargoQueue.size - QUEUE_DISPLAY_LIMIT} more.")
    println("------------------------------------------------------------")
}
