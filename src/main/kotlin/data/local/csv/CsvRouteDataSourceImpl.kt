
package data.local.csv

import data.local.dataholder.RouteRaw
import data.local.datasource.CsvRouteDataSource

class CsvRouteDataSourceImpl(
    csvHandler: CsvFileHandler,
) : BaseCsvDataSource<RouteRaw>(csvHandler), CsvRouteDataSource {

    override suspend fun getAllRoutes(): List<RouteRaw> = readAll()

    override fun parseLine(line: String): RouteRaw? =
        splitFields(line)
            .takeIf { it.size == EXPECTED_ROUTE_FIELDS }
            ?.let(::mapFieldsToRoute)

    private fun mapFieldsToRoute(fields: List<String>): RouteRaw? {
        val rawRouteId = fields[INDEX_ROUTE_ID].uppercase()
        val rawOriginHub = fields[INDEX_ORIGIN_HUB].uppercase()
        val rawDestHub = fields[INDEX_DESTINATION_HUB].uppercase()
        val distanceKm = parseDistance(fields[INDEX_DISTANCE])
        val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].toIntOrNull()

        if (distanceKm == null || typicalDelayMin == null) return null

        val hasEmptyText = listOf(
            rawRouteId,
            rawOriginHub,
            rawDestHub
        ).any(String::isBlank)

        return fields
            .takeIf { !hasEmptyText }
            ?.let {
                RouteRaw(
                    routeId = normalizePrefix(rawRouteId, ROUTE_PREFIX),
                    originHubId = normalizePrefix(rawOriginHub, WAREHOUSE_PREFIX),
                    destinationHubId = normalizePrefix(rawDestHub, WAREHOUSE_PREFIX),
                    distanceKm = distanceKm,
                    typicalDelayMin = typicalDelayMin
                )
            }
    }

    private fun parseDistance(distance: String): Double? =
        distance.replace(DISTANCE_UNIT_KM, "", ignoreCase = true)
            .trim()
            .toDoubleOrNull()

    private companion object {
        const val EXPECTED_ROUTE_FIELDS = 5
        const val DISTANCE_UNIT_KM = "km"

        const val INDEX_ROUTE_ID = 0
        const val INDEX_ORIGIN_HUB = 1
        const val INDEX_DESTINATION_HUB = 2
        const val INDEX_DISTANCE = 3
        const val INDEX_TYPICAL_DELAY = 4

        const val ROUTE_PREFIX = "RT-"
        const val WAREHOUSE_PREFIX = "WH-"
    }
}
