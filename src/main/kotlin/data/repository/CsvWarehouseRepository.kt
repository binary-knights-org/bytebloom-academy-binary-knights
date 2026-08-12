package data.repository

import data.dataholder.WarehouseRaw
import data.reader.CsvFileReader
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields
import domain.repository.WarehouseRepository

private const val EXPECTED_WAREHOUSE_FIELDS = 5
private const val CSV_DELIMITER = ","

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2
private const val INDEX_LATITUDE = 3
private const val INDEX_LONGITUDE = 4

class CsvWarehouseRepository(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader()
) : WarehouseRepository {

    override fun getAllWarehouses(): List<WarehouseRaw> {
        val lines = reader.readLines(filePath)
        return extractWarehouses(lines)
    }

    private fun extractWarehouses(lines: List<String>): List<WarehouseRaw> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
    }

    private fun parseLine(line: String): WarehouseRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_WAREHOUSE_FIELDS)) return null

        return mapFieldsToWarehouse(fields)
    }

    private fun mapFieldsToWarehouse(fields: List<String>): WarehouseRaw? {
        val hubId = fields[INDEX_ID]
        val hubName = fields[INDEX_NAME]
        val regionalZone = fields[INDEX_REGIONAL_ZONE]
        val latitude = fields[INDEX_LATITUDE].toDoubleOrNull()
        val longitude = fields[INDEX_LONGITUDE].toDoubleOrNull()

        return when {
            (latitude == null || longitude == null) -> null
            else -> WarehouseRaw(hubId, hubName, regionalZone, latitude, longitude)
        }
    }
}
