package parser

import dataholder.RouteRaw
import java.io.File

private const val EXPECTED_ROUTE_FIELDS = 5
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1
private const val DISTANCE_UNIT_KM = "km"

private const val INDEX_ROUTE_ID = 0
private const val INDEX_ORIGIN_HUB = 1
private const val INDEX_DESTINATION_HUB = 2
private const val INDEX_DISTANCE = 3
private const val INDEX_TYPICAL_DELAY = 4

fun loadRouteData(filePath: String): List<RouteRaw> {
    val file = File(filePath)
    if (checkFileExists(file, "RouteParser")) {
        return emptyList()
    }
    val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
    return extractRoutes(lines)
}

private fun extractRoutes(lines: List<String>): List<RouteRaw> {
    return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
}

private fun parseDistance(distance: String): Double? {
    val cleanDistance = distance.replace(DISTANCE_UNIT_KM, "", ignoreCase = true).trim()
    return cleanDistance.toDoubleOrNull()
}

private fun mapFieldsToRoutes(fields: List<String>, rawLine: String): RouteRaw? {
    val routeId = fields[INDEX_ROUTE_ID]
    val originHubId = fields[INDEX_ORIGIN_HUB]
    val destinationHubId = fields[INDEX_DESTINATION_HUB]
    val distanceKm = parseDistance(fields[INDEX_DISTANCE]) ?: return skipInvalidRow("RouteParser", rawLine)
    val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].toIntOrNull() ?: return skipInvalidRow("RouteParser", rawLine)

    return RouteRaw(routeId, originHubId, destinationHubId, distanceKm, typicalDelayMin)
}

private fun parseLine(line: String): RouteRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields, EXPECTED_ROUTE_FIELDS, "RouteParser", line)) return null
    return mapFieldsToRoutes(fields, line)
}
