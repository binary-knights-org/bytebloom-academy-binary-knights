package routes

import dataholders.Route
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

private fun parseDistance(distance: String): Double? {
    val cleanDistance = distance.replace(DISTANCE_UNIT_KM, "", ignoreCase = true).trim()
    return cleanDistance.toDoubleOrNull()
}

private fun parseRouteLine(line: String): Route? {
    if (line.isBlank()) return null
    val fields = line.split(CSV_DELIMITER).map { it.trim() }

    if (fields.size != EXPECTED_ROUTE_FIELDS) {
        println("A broken line was skipped: $line")
        return null
    }

    val routeId = fields[INDEX_ROUTE_ID]
    val originHubId = fields[INDEX_ORIGIN_HUB]
    val destinationHubId = fields[INDEX_DESTINATION_HUB]

    val distanceKm = parseDistance(fields[INDEX_DISTANCE])
    val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].toIntOrNull()

    if (distanceKm == null || typicalDelayMin == null) {
        println("Line conversion error: Invalid numeric data in line $line")
        return null
    }

    return Route(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}

fun loadRouteData(filePath: String): List<Route> {
    val routeFile = File(filePath)
    if (!routeFile.exists()) {
        println("Warning: The track file is missing at path: $filePath")
        return emptyList()
    }

    val validRoutes = mutableListOf<Route>()

    try {
        val lines = routeFile.readLines().drop(HEADER_LINES_TO_SKIP)
        for (line in lines) {
            val route = parseRouteLine(line)
            if (route != null) validRoutes.add(route)
        }
    } catch (e: Exception) {
        println("Diagnostic Error: Failed to read routes file due to: ${e.message}")
    }
    return validRoutes
}