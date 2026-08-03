package data.parser

import data.dataholder.FleetRaw
import java.io.File

private const val EXPECTED_FLEET_FIELDS = 4
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_VEHICLE_ID = 0
private const val INDEX_CURRENT_HUB_ID = 1
private const val INDEX_MAX_CAPACITY = 2
private const val INDEX_COST_PER_KM = 3


fun loadFleetData(filePath: String): List<FleetRaw> {
    val file = File(filePath)
    if (!checkFileExists(file)) {
        return emptyList()
    }

    val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
    return extractFleets(lines)
}


private fun extractFleets(lines: List<String>): List<FleetRaw> {
    return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
}

private fun mapFieldsToFleet(fields: List<String>): FleetRaw? {
    val vehicleIds = fields[INDEX_VEHICLE_ID]
    val currentHubId = fields[INDEX_CURRENT_HUB_ID]
    val maxCapacity = fields[INDEX_MAX_CAPACITY].toDoubleOrNull()
    val costPerKm = fields[INDEX_COST_PER_KM].toDoubleOrNull()

    return when {
        (maxCapacity == null || costPerKm == null) -> null
        else -> FleetRaw(listOf(vehicleIds), currentHubId, maxCapacity, costPerKm)
    }
}

private fun parseLine(line: String): FleetRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields, EXPECTED_FLEET_FIELDS)) return null
    return mapFieldsToFleet(fields)
}
