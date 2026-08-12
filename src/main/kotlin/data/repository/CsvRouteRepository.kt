package data.repository

import data.dataholder.RouteRaw
import data.reader.CsvFileReader
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields
import domain.repository.RouteRepository

private const val EXPECTED_ROUTE_FIELDS = 5
private const val CSV_DELIMITER = ","
private const val DISTANCE_UNIT_KM = "km"

private const val INDEX_ROUTE_ID = 0
private const val INDEX_ORIGIN_HUB = 1
private const val INDEX_DESTINATION_HUB = 2
private const val INDEX_DISTANCE = 3
private const val INDEX_TYPICAL_DELAY = 4

class CsvRouteRepository(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader()
) : RouteRepository {

    override fun getAllRoutes(): List<RouteRaw> {
        val lines = reader.readLines(filePath)
        return extractRoutes(lines)
    }

    private fun extractRoutes(lines: List<String>): List<RouteRaw> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
    }

    private fun parseLine(line: String): RouteRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_ROUTE_FIELDS)) return null
        return mapFieldsToRoute(fields)
    }

    private fun mapFieldsToRoute(fields: List<String>): RouteRaw? {
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
