package parser

import dataholder.FleetRaw
import java.io.File

private const val EXPECTED_FLEET_COLUMNS = 4
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_VEHICLE_ID = 0
private const val INDEX_CURRENT_HUB_ID = 1
private const val INDEX_MAX_CAPACITY = 2
private const val INDEX_COST_PER_KM = 3


fun loadFleetData(filePath: String): List<FleetRaw> {
    val fleetFile = File(filePath)
    if (isMissingFile(fleetFile)) {
        return emptyList()
    }

    val lines = fleetFile.readLines().drop(HEADER_LINES_TO_SKIP)
    return processFleetLines(lines)
}

private fun isMissingFile(file: File): Boolean {
    if (!file.exists()) {
        println("WARNING (FleetParser): File not found at path: ${file.path}")
        return true
    }
    return false
}


private fun processFleetLines(lines: List<String>): List<FleetRaw> {
    val fleetList = mutableListOf<FleetRaw>()

    for (line in lines) {
        if (line.isBlank()) continue
        val fleet = parseFleetLine(line)
        if (fleet != null) {
            fleetList.add(fleet)
        }
    }

    return fleetList
}

private fun parseFleetLine(line: String): FleetRaw? {
    if (line.isBlank()) return null
    val columns = line.split(CSV_DELIMITER).map { it.trim() }

    if (columns.size != EXPECTED_FLEET_COLUMNS) {
        println("WARNING (FleetParser): Skipping malformed row (expected $EXPECTED_FLEET_COLUMNS fields): $line")
        return null
    }

    val vehicleId = columns[INDEX_VEHICLE_ID].trim()
    val currentHubId = columns[INDEX_CURRENT_HUB_ID].trim()
    val maxCapacity = columns[INDEX_MAX_CAPACITY].trim().toDoubleOrNull()
    val costPerKm = columns[INDEX_COST_PER_KM].trim().toDoubleOrNull()

    if (maxCapacity == null || costPerKm == null) {
        println("WARNING (FleetParser): Skipping row (invalid numeric data): $line")
        return null
    }

    return FleetRaw(listOf(vehicleId), currentHubId, maxCapacity, costPerKm)
}
