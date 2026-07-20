package parsers
import dataholders.Fleet
import java.io.File

fun loadFleetData(filePath: String): List<Fleet> {
    val fleetList = mutableListOf<Fleet>()
    val file = File(filePath)

    try {
    if (!file.exists()) {
        println("Diagnostic Warning: Fleet file not found at $filePath")
        return fleetList
    }
        val lines = file.readLines()

        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) {
                continue
            }
            val columns = line.split(",")
            val EXPECTED_FLEET_COLUMNS =4
            if (columns.size != EXPECTED_FLEET_COLUMNS) {
                continue
            }
            val vehicleId = columns[0].trim()
            val currentHubId = columns[1].trim()
            val maxCapacity = columns[2].toDoubleOrNull()
            val costPerKm = columns[3].toDoubleOrNull()
            if (maxCapacity == null || costPerKm == null) {
                continue
            }
            fleetList.add(Fleet(vehicleId, currentHubId, maxCapacity, costPerKm))
        }


    }catch (e:Exception){
    print("Diagnostic Error: Failed to parse fleet CSV due to: ${e.message}")}
   return fleetList
}
