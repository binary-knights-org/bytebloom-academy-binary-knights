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
            val column = line.split(",")
            if (column.size != 4) {
                continue
            }
            val vehicleId = column[0].trim()
            val currentHubId = column[1].trim()
            val maxCapacityStr = column[2].toDoubleOrNull()
            val costPerKmStr = column[3].toDoubleOrNull()
            if (maxCapacityStr == null || costPerKmStr == null) {
                continue
            }
            val fleet = Fleet(vehicleId, currentHubId, maxCapacityStr, costPerKmStr)
            fleetList.add(fleet)
        }


    }catch (e:Exception){
    print("Diagnostic Error: Failed to parse fleet CSV due to: ${e.message}")}
   return fleetList
}
