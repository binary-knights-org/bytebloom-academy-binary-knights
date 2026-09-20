package data.local.csv

import data.local.dataholder.VehicleRaw
import data.local.datasource.CsvVehicleDataSource

class CsvVehicleDataSourceImpl(
    private val csvHandler: CsvFileHandler,
) : CsvVehicleDataSource {

    override fun getAllVehicles(): List<VehicleRaw> {
        return try {
            val lines = csvHandler.readLines()
            lines.mapNotNull { parseLine(it) }
        } catch (_: CsvFileNotFoundException) {
            emptyList()
        }
    }

    fun parseLine(line: String): VehicleRaw? {
        val fields = csvHandler.splitFields(line, CSV_DELIMITER)

        if (fields.size != EXPECTED_VEHICLE_FIELDS) {
            return null
        }

        return mapFieldsToVehicle(fields)
    }

    private fun mapFieldsToVehicle(fields: List<String>): VehicleRaw? {
        val rawVehicleId = fields[INDEX_VEHICLE_ID].trim().uppercase()
        val rawCurrentHubId = fields[INDEX_CURRENT_HUB_ID].trim().uppercase()

        if (rawVehicleId.isBlank() || rawCurrentHubId.isBlank()) return null

        val vehicleId = if (rawVehicleId.startsWith("TRK-")) rawVehicleId else "TRK-$rawVehicleId"
        val currentHubId = if (rawCurrentHubId.startsWith("WH-")) rawCurrentHubId else "WH-$rawCurrentHubId"

        val maxCapacity = fields[INDEX_MAX_CAPACITY].trim().toDoubleOrNull()
        val costPerKm = fields[INDEX_COST_PER_KM].trim().toDoubleOrNull()

        return when {
            maxCapacity == null || costPerKm == null -> null
            else -> VehicleRaw(
                vehicleIds = listOf(vehicleId),
                currentHubId = currentHubId,
                maxCapacityKg = maxCapacity,
                costPerKm = costPerKm
            )
        }
    }

    private companion object {
        const val EXPECTED_VEHICLE_FIELDS = 4
        const val CSV_DELIMITER = ","

        const val INDEX_VEHICLE_ID = 0
        const val INDEX_CURRENT_HUB_ID = 1
        const val INDEX_MAX_CAPACITY = 2
        const val INDEX_COST_PER_KM = 3
    }
}
