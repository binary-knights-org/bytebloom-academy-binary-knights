package data.repository

import data.dataholder.RouteRaw
import data.parser.checkFileExists
import data.parser.hasValidFieldCount
import data.parser.parseCsvFields
import domain.repository.RouteRepository
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

class CsvRouteRepository(
    private val filePath: String
) : RouteRepository {

    override fun getRoutes(): List<RouteRaw> {
        val file = File(filePath)
        if (!checkFileExists(file)) {
            return emptyList()
        }
        val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
        return extractRoutes(lines)
    }

    private fun extractRoutes(lines: List<String>): List<RouteRaw> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
    }

    private fun parseLine(line: String): RouteRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_ROUTE_FIELDS)) return null
        return mapFieldsToRoutes(fields)
    }

    private fun mapFieldsToRoutes(fields: List<String>): RouteRaw? {
        val routeId = fields[INDEX_ROUTE_ID]
        val originHubId = fields[INDEX_ORIGIN_HUB]
        val destinationHubId = fields[INDEX_DESTINATION_HUB]
        val distanceKm = parseDistance(fields[INDEX_DISTANCE])
        val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].toIntOrNull()

        return when {
            (distanceKm == null || typicalDelayMin == null) -> null
            else -> RouteRaw(routeId, originHubId, destinationHubId, distanceKm, typicalDelayMin)
        }
    }

    private fun parseDistance(distance: String): Double? {
        val cleanDistance = distance.replace(DISTANCE_UNIT_KM, "", ignoreCase = true).trim()
        return cleanDistance.toDoubleOrNull()
    }
}
