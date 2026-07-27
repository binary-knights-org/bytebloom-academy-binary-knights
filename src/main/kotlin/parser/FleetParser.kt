package parser

import dataholder.FleetRaw
import utils.hasValidFieldCount
import utils.parseCsvFields
import java.io.File

private const val EXPECTED_FLEET_FIELDS = 4
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

private fun mapFieldsToFleet(fields: List<String>, rawLine: String): FleetRaw? {
    val vehicleIds = fields[INDEX_VEHICLE_ID]
    val currentHubId = fields[INDEX_CURRENT_HUB_ID]
    val maxCapacity = fields[INDEX_MAX_CAPACITY].toDoubleOrNull()
    val costPerKm = fields[INDEX_COST_PER_KM].toDoubleOrNull()

    if (hasValidNumericData(maxCapacity, costPerKm, rawLine)) return null

    return FleetRaw(listOf(vehicleIds), currentHubId, maxCapacity, costPerKm)
}

private fun hasValidNumericData(maxCapacity: Double?, costPerKm: Double?, rawLine: String): Boolean {
    if (maxCapacity == null || costPerKm == null) {
        println("WARNING (FleetParser): Skipping row (invalid numeric data): $rawLine")
        return false
    }
    return true
}

private fun parseFleetLine(line: String): FleetRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields, EXPECTED_FLEET_FIELDS, "FleetParser", line)) return null
    return mapFieldsToFleet(fields, line)
}
