package data.repository

import data.dataholder.FleetRaw
import data.reader.CsvFileReader
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields
import domain.repository.FleetRepository

private const val EXPECTED_FLEET_FIELDS = 4
private const val CSV_DELIMITER = ","

private const val INDEX_VEHICLE_ID = 0
private const val INDEX_CURRENT_HUB_ID = 1
private const val INDEX_MAX_CAPACITY = 2
private const val INDEX_COST_PER_KM = 3

class CsvFleetRepository(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader()
) : FleetRepository {

    override fun getAllFleets(): List<FleetRaw> {
        val lines = reader.readLines(filePath)
        return extractFleets(lines)
    }

    private fun extractFleets(lines: List<String>): List<FleetRaw> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
    }

    private fun parseLine(line: String): FleetRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_FLEET_FIELDS)) return null
        return mapFieldsToFleet(fields)
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
}
