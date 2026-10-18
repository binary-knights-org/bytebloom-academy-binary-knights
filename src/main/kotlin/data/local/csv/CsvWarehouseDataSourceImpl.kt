
package data.local.csv

import data.local.dataholder.WarehouseRaw
import data.local.datasource.CsvWarehouseDataSource

class CsvWarehouseDataSourceImpl(
    csvHandler: CsvFileHandler,
) : BaseCsvDataSource<WarehouseRaw>(csvHandler), CsvWarehouseDataSource {

    override suspend fun getAllWarehouses(): List<WarehouseRaw> = readAll()

    override fun parseLine(line: String): WarehouseRaw? =
        splitFields(line)
            .takeIf { it.size == EXPECTED_WAREHOUSE_FIELDS }
            ?.let(::mapFieldsToWarehouse)

    private fun mapFieldsToWarehouse(fields: List<String>): WarehouseRaw? {
        val rawHubId = fields[INDEX_ID].uppercase()
        val hubName = fields[INDEX_NAME].uppercase()
        val regionalZone = fields[INDEX_REGIONAL_ZONE].uppercase()
        val latitude = fields[INDEX_LATITUDE].toDoubleOrNull()
        val longitude = fields[INDEX_LONGITUDE].toDoubleOrNull()

        if (latitude == null || longitude == null) return null

        val hasEmptyText = listOf(
            rawHubId,
            hubName,
            regionalZone
        ).any(String::isBlank)

        return fields
            .takeIf { !hasEmptyText }
            ?.let {
                WarehouseRaw(
                    hubId = normalizePrefix(rawHubId, WAREHOUSE_PREFIX),
                    hubName = hubName,
                    regionalZone = regionalZone,
                    latitude = latitude,
                    longitude = longitude
                )
            }
    }

    private companion object {
        const val EXPECTED_WAREHOUSE_FIELDS = 5

        const val INDEX_ID = 0
        const val INDEX_NAME = 1
        const val INDEX_REGIONAL_ZONE = 2
        const val INDEX_LATITUDE = 3
        const val INDEX_LONGITUDE = 4

        const val WAREHOUSE_PREFIX = "WH-"
    }
}
