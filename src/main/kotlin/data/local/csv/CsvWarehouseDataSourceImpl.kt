package data.local.csv

import data.local.dataholder.WarehouseRaw
import data.local.datasource.CsvWarehouseDataSource

class CsvWarehouseDataSourceImpl(
    private val csvHandler: CsvFileHandler,
) : CsvWarehouseDataSource {
    override fun getAllWarehouses(): List<WarehouseRaw> {
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
        val rawHubId = fields[INDEX_ID].trim().uppercase()
        val hubName = fields[INDEX_NAME].trim().uppercase()
        val regionalZone = fields[INDEX_REGIONAL_ZONE].trim().uppercase()

        if (rawHubId.isBlank() || hubName.isBlank() || regionalZone.isBlank()) return null

        val hubId = if (rawHubId.startsWith("WH-")) rawHubId else "WH-$rawHubId"

        val latitude = fields[INDEX_LATITUDE].trim().toDoubleOrNull()
        val longitude = fields[INDEX_LONGITUDE].trim().toDoubleOrNull()

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
