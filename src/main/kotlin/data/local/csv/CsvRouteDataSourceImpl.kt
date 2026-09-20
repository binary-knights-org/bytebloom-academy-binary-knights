package data.local.csv

import data.local.dataholder.RouteRaw
import data.local.datasource.CsvRouteDataSource

class CsvRouteDataSourceImpl(
    private val csvHandler: CsvFileHandler
) : CsvRouteDataSource {
    override fun getAllRoutes(): List<RouteRaw> {
        return try {
            val lines = csvHandler.readLines()
            lines.mapNotNull { parseLine(it) }
        } catch (_: CsvFileNotFoundException) {
            emptyList()
        }
    }

    fun parseLine(line: String): RouteRaw? {
        val fields = csvHandler.splitFields(line, CSV_DELIMITER)
        if (fields.size != EXPECTED_ROUTE_FIELDS) {
            return null
        }
        return mapFieldsToRoute(fields)
    }

    private fun mapFieldsToRoute(fields: List<String>): RouteRaw? {
        val rawRouteId = fields[INDEX_ROUTE_ID].trim().uppercase()
        val rawOriginHub = fields[INDEX_ORIGIN_HUB].trim().uppercase()
        val rawDestHub = fields[INDEX_DESTINATION_HUB].trim().uppercase()

        if (rawRouteId.isBlank() || rawOriginHub.isBlank() || rawDestHub.isBlank()) return null

        val routeId = if (rawRouteId.startsWith("RT-")) rawRouteId else "RT-$rawRouteId"
        val originHubId = if (rawOriginHub.startsWith("WH-")) rawOriginHub else "WH-$rawOriginHub"
        val destinationHubId = if (rawDestHub.startsWith("WH-")) rawDestHub else "WH-$rawDestHub"

        val distanceKm = parseDistance(fields[INDEX_DISTANCE])
        val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].trim().toIntOrNull()

        return when {
            distanceKm == null || typicalDelayMin == null -> null
            else -> RouteRaw(
                routeId = routeId,
                originHubId = originHubId,
                destinationHubId = destinationHubId,
                distanceKm = distanceKm,
                typicalDelayMin = typicalDelayMin
            )
        }
    }

    private fun parseDistance(distance: String): Double? {
        val cleanDistance = distance.replace(DISTANCE_UNIT_KM, "", ignoreCase = true).trim()
        return cleanDistance.toDoubleOrNull()
    }

    private companion object {
        const val EXPECTED_ROUTE_FIELDS = 5
        const val CSV_DELIMITER = ","
        const val DISTANCE_UNIT_KM = "km"

        const val INDEX_ROUTE_ID = 0
        const val INDEX_ORIGIN_HUB = 1
        const val INDEX_DESTINATION_HUB = 2
        const val INDEX_DISTANCE = 3
        const val INDEX_TYPICAL_DELAY = 4
    }
}
