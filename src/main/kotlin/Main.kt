import parser.loadFleetData
import parser.loadPackageData
import parser.loadRouteData
import parser.loadWarehouseData

import domain.builder.NetworkBuilder
import domain.builder.NetworkDataLoader

private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val FLEET_FILE_PATH = "src/main/resources/fleet.csv"


fun main() {

    val networkBuilder = initializeNetwork()
    val isMemoryMatch = verifyHeapLoopResolution(networkBuilder)
    printVerificationReport(isMemoryMatch)
}

private fun initializeNetwork(): NetworkBuilder {
    val rawFleets = loadFleetData(FLEET_FILE_PATH)
    val rawPackages = loadPackageData(PACKAGE_FILE_PATH)
    val rawRoutes = loadRouteData(ROUTES_FILE_PATH)
    val rawWarehouses = loadWarehouseData(WAREHOUSES_FILE_PATH)

    println("\nBuilding network graph (Safe Instantiation)...")
    val builder = NetworkBuilder()
    val loader = NetworkDataLoader(builder)

    loader.loadData(rawFleets, rawPackages, rawRoutes, rawWarehouses)

    println("Network built successfully!\n")
    return builder
}

private fun verifyHeapLoopResolution(builder: NetworkBuilder): Boolean {
    val testWarehouse = builder.getAllWarehouses().first()
    val testVehicle = testWarehouse.stationedVehicles.first()

    return testWarehouse === testVehicle.currentHub
}

private fun printVerificationReport(isMatch: Boolean) {
    if (isMatch) {
        println("Result: SUCCESS")
        println("Warehouse Object Reference Match: TRUE")
        println("Note: Circular dependency resolved safely without StackOverflow.")
    } else {
        println("Result: FAILED")
        println("Warehouse Object Reference Match: FALSE")
        println("Note: Memory addresses do not match, check your instantiation logic.")
    }
}