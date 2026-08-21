package data.repository

import data.dataholder.RouteRaw
import data.mapper.toDomain
import data.reader.CsvFileReader
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields
import domain.model.Route
import domain.model.Warehouse
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

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
    private val warehouseRepository: WarehouseRepository,
    private val reader: CsvFileReader = CsvFileReader()
) : RouteRepository {

    override fun getAllRoutes(): List<Route> {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }
        val lines = reader.readLines(filePath)
        return extractRoutes(lines, warehousesById)
    }

    private fun extractRoutes(
        lines: List<String>,
        warehousesById: Map<String, Warehouse>
    ): List<Route> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it)?.toDomain(warehousesById) }
    }

    private fun parseLine(line: String): RouteRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_ROUTE_FIELDS)) {
            return null
        }

        return mapFieldsToRoute(fields)
    }

    private fun mapFieldsToRoute(fields: List<String>): RouteRaw? {
        val routeId = fields[INDEX_ROUTE_ID]
        val originHubId = fields[INDEX_ORIGIN_HUB]
        val destinationHubId = fields[INDEX_DESTINATION_HUB]
        val distanceKm = parseDistance(fields[INDEX_DISTANCE])
        val typicalDelayMin = fields[INDEX_TYPICAL_DELAY].toIntOrNull()

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
}
