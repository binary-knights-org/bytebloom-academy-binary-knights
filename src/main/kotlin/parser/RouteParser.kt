package routes

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
    val routeFile = File(filePath)
    if (isMissingFile(routeFile)) {
        return emptyList()
    }
    val lines = routeFile.readLines().drop(HEADER_LINES_TO_SKIP)
    return processRouteLines(lines)
}

private fun isMissingFile(file: File): Boolean {
    if (!file.exists()) {
        println("WARNING (RouteParser): File not found at path: ${file.path}")
        return true
    }
    return false
}

private fun processRouteLines(lines: List<String>): List<RouteRaw> {
    val validRoutes = mutableListOf<RouteRaw>()

    for (line in lines) {
        if (line.isBlank()) continue
        val route = parseRouteLine(line)
        if (route != null) {
            validRoutes.add(route)
        }
    }

    return validRoutes
}

private fun parseDistance(distance: String): Double? {
    val cleanDistance = distance.replace(DISTANCE_UNIT_KM, "", ignoreCase = true).trim()
    return cleanDistance.toDoubleOrNull()
}

private fun parseRouteLine(line: String): RouteRaw? {
    if (line.isBlank()) return null
    val fields = line.split(CSV_DELIMITER).map { it.trim() }

    if (fields.size != EXPECTED_ROUTE_FIELDS) {
        println("WARNING (RouteParser): Skipping malformed row (expected $EXPECTED_ROUTE_FIELDS fields): $line")
        return null
    }

    val routeId = fields[INDEX_ROUTE_ID]
    val originHubId = fields[INDEX_ORIGIN_HUB]
    val destinationHubId = fields[INDEX_DESTINATION_HUB]

    val distanceKm = parseDistance(fields[INDEX_DISTANCE])
    val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].toIntOrNull()

    if (distanceKm == null || typicalDelayMin == null) {
        println("WARNING (RouteParser): Skipping row (invalid numeric data): $line")
        return null
    }

    return RouteRaw(routeId, originHubId, destinationHubId, distanceKm, typicalDelayMin)
}
