package data.local.csv

import data.dataholder.WarehouseRaw
import data.datasource.WarehouseDataSource

class CsvWarehouseDataSource(
    private val csvHandler: CsvFileHandler,
) : WarehouseDataSource {
    override fun getRawWarehouses(): List<WarehouseRaw> {
        return try {
            val lines = csvHandler.readLines()
            lines.mapNotNull { parseLine(it) }
        } catch (_: CsvFileNotFoundException) {
            emptyList()
        }
    }

    fun parseLine(line: String): WarehouseRaw? {
        val fields = csvHandler.splitFields(line, CSV_DELIMITER)
        if (fields.size != EXPECTED_WAREHOUSE_FIELDS) {
            return null
        }
        return mapFieldsToWarehouse(fields)
    }

    private fun mapFieldsToWarehouse(fields: List<String>): WarehouseRaw? {
        val hubId = fields[INDEX_ID]
        val hubName = fields[INDEX_NAME]
        val regionalZone = fields[INDEX_REGIONAL_ZONE]
        val latitude = fields[INDEX_LATITUDE].toDoubleOrNull()
        val longitude = fields[INDEX_LONGITUDE].toDoubleOrNull()

        return when {
            latitude == null || longitude == null -> null
            else -> WarehouseRaw(
                hubId = hubId,
                hubName = hubName,
                regionalZone = regionalZone,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    private companion object {
        const val EXPECTED_WAREHOUSE_FIELDS = 5
        const val CSV_DELIMITER = ","

        const val INDEX_ID = 0
        const val INDEX_NAME = 1
        const val INDEX_REGIONAL_ZONE = 2
        const val INDEX_LATITUDE = 3
        const val INDEX_LONGITUDE = 4

    }
}
