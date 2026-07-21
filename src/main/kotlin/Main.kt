import dataholders.Package
import parsers.loadFleetData
import parsers.loadPackageData
import parsers.loadWarehouseData
import routes.loadRouteData
import sorter.sortPackagesByPriorityAndWeight

private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val FLEET_FILE_PATH = "src/main/resources/fleet.csv"

private const val TOP_SHIPMENTS_LIMIT = 3


private fun printParsingReport(
    packageCount: Int,
    warehousesCount: Int,
    routesCount: Int,
    fleetCount: Int
) {
    println("\n--- Data Parsing Report ---")
    println(" Successfully parsed Packages: $packageCount records.")
    println(" Successfully parsed Warehouses: $warehousesCount records.")
    println(" Successfully parsed Routes: $routesCount records.")
    println(" Successfully parsed Fleet: $fleetCount vehicle records.")
}

private fun printTopShipments(packages: List<Package>, limit: Int) {
    println("\n--- Executing Manual Package Sorting ---")
    println("\n--- Top $limit Priority Shipments ---")

    packages.take(limit).forEachIndexed { index, pkg ->
        val packageNumber = index + 1
        println("package = $packageNumber , id = ${pkg.id} , destinationHub = ${pkg.destinationHubId} , weight = ${pkg.weight} kg , priority = ${pkg.priority}")
    }
}

fun main() {

    val packageList = loadPackageData(PACKAGE_FILE_PATH)
    val warehousesList = loadWarehouseData(WAREHOUSES_FILE_PATH)
    val routesList = loadRouteData(ROUTES_FILE_PATH)
    val fleetList = loadFleetData(FLEET_FILE_PATH)

    val sortedPackages = sortPackagesByPriorityAndWeight(packageList)

    printParsingReport(packageList.size, warehousesList.size, routesList.size, fleetList.size)
    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)
}
