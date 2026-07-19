import parsers.loadFleetData
import org.example.Logic.parseWarehouses

import parsers.parsePackage
import routes.parseRoutes
import sorter.sortPackagesByPriorityAndWeight

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    val packageFilePath = "src/main/resources/packages.csv"
    val warehousesFilePath = "src/main/resources/warehouses.csv"
    val routesFilePath = "src/main/resources/routes.csv"
    val fleetFilePath = "src/main/resources/fleet.csv"



    val packageList = mutableListOf<dataholders.Package>()
    val packageFile = java.io.File(packageFilePath)
    if (packageFile.exists()) {
        val lines = packageFile.readLines()
        for (i in 1 until  lines.size){
            val line = lines[i].trim()
            if (line.isEmpty()) continue
            val parsedPackage = parsePackage(line)
            if (parsedPackage != null) {
                packageList.add(parsedPackage)
            }

        }
    }



    val warehousesList = parseWarehouses(warehousesFilePath)
    val routesList = parseRoutes(routesFilePath)
    val fleetList = loadFleetData(fleetFilePath)
    val sortedPackages = sortPackagesByPriorityAndWeight(packageList)


    println("\n---  Data Parsing Report ---")
    println(" Successfully parsed Packages: ${packageList.size} records.")
    println(" Successfully parsed Warehouses: ${warehousesList.size} records.")
    println(" Successfully parsed Routes: ${routesList.size} records.")
    println(" Successfully parsed Fleet: ${fleetList.size} vehicle records.")


    println("\n---  Executing Manual Package Sorting ---")

    println("\n---  Top 3 Priority Shipments ---")

    var packageCount = 1
    for (pkg in sortedPackages) {
        if (packageCount >= 4) break
        println("package = $packageCount , id = ${pkg.id} , destinationHub = ${pkg.destinationHubId} , weight = ${pkg.weight} kg , priority = ${pkg.priority}")
        packageCount++
    }

}