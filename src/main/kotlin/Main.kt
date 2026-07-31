import algorithm.sortPackagesByImportance
import dataholder.PackageRaw
import domain.builder.DomainGraphBuilder
import domain.model.Warehouse
import parser.loadFleetData
import parser.loadPackageData
import parser.loadRouteData
import parser.loadWarehouseData



private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val FLEET_FILE_PATH = "src/main/resources/fleet.csv"

private const val TOP_SHIPMENTS_LIMIT = 3


private fun printParsingReport(
    fleetCount: Int,
    packageCount: Int,
    routesCount: Int,
    warehousesCount: Int
) {
    println("\n--- Data Parsing Report ---")
    println(" Successfully parsed Fleet: $fleetCount vehicle records.")
    println(" Successfully parsed Packages: $packageCount records.")
    println(" Successfully parsed Routes: $routesCount records.")
    println(" Successfully parsed Warehouses: $warehousesCount records.")
}

private fun printTopShipments(packages: List<PackageRaw>, limit: Int) {
    println("\n--- Executing Manual Package Sorting ---")
    println("\n--- Top $limit Priority Shipments ---")

    packages.take(limit).forEachIndexed { index, pkg ->
        val packageNumber = index + 1
        println("package = $packageNumber" +
                " , id = ${pkg.packageId}" +
                " , destinationHub = ${pkg.destinationHubId}" +
                " , weight = ${pkg.weight}" +
                " kg , priority = ${pkg.priority}")
    }
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

fun main() {

    val fleetList = loadFleetData(FLEET_FILE_PATH)
    val packageList = loadPackageData(PACKAGE_FILE_PATH)
    val routesList = loadRouteData(ROUTES_FILE_PATH)
    val warehousesList = loadWarehouseData(WAREHOUSES_FILE_PATH)

    printParsingReport(
        fleetList.size,
        packageList.size,
        routesList.size,
        warehousesList.size
    )
    val sortedPackages = sortPackagesByImportance(packageList)

    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)

    val graphBuilder = DomainGraphBuilder()
    val graph = graphBuilder.buildGraph(
        rawVehicles = fleetList,
        rawPackages = packageList,
        rawRoutes = routesList,
        rawWarehouses = warehousesList
    )

    printGraphSummary(graph)
}
