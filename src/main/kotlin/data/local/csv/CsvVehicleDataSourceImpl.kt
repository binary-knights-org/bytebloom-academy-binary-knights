
package data.local.csv

import data.local.dataholder.VehicleRaw
import data.local.datasource.CsvVehicleDataSource

class CsvVehicleDataSourceImpl(
    csvHandler: CsvFileHandler,
) : BaseCsvDataSource<VehicleRaw>(csvHandler), CsvVehicleDataSource {

    override suspend fun getAllVehicles(): List<VehicleRaw> = readAll()

    override fun parseLine(line: String): VehicleRaw? =
        splitFields(line)
            .takeIf { it.size == EXPECTED_VEHICLE_FIELDS }
            ?.let(::mapFieldsToVehicle)

    private fun mapFieldsToVehicle(fields: List<String>): VehicleRaw? {
        val rawVehicleId = fields[INDEX_VEHICLE_ID].uppercase()
        val rawCurrentHubId = fields[INDEX_CURRENT_HUB_ID].uppercase()
        val maxCapacity = fields[INDEX_MAX_CAPACITY].toDoubleOrNull()
        val costPerKm = fields[INDEX_COST_PER_KM].toDoubleOrNull()

        if (maxCapacity == null || costPerKm == null) return null

        val hasEmptyText = listOf(
            rawVehicleId,
            rawCurrentHubId
        ).any(String::isBlank)

        return fields
            .takeIf { !hasEmptyText }
            ?.let {
                VehicleRaw(
                    vehicleIds = listOf(
                        normalizePrefix(rawVehicleId, VEHICLE_PREFIX)
                    ),
                    currentHubId = normalizePrefix(
                        rawCurrentHubId,
                        WAREHOUSE_PREFIX
                    ),
                    maxCapacityKg = maxCapacity,
                    costPerKm = costPerKm
                )
            }
    }

    private companion object {
        const val EXPECTED_VEHICLE_FIELDS = 4

        const val INDEX_VEHICLE_ID = 0
        const val INDEX_CURRENT_HUB_ID = 1
        const val INDEX_MAX_CAPACITY = 2
        const val INDEX_COST_PER_KM = 3

        const val VEHICLE_PREFIX = "TRK-"
        const val WAREHOUSE_PREFIX = "WH-"
    }
}
