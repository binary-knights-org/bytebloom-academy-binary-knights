package data.local.csv

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource


class CsvVehicleDataSource(
    private val csvHandler: CsvFileHandler,
) : VehicleDataSource {
    override fun getRawVehicles(): List<VehicleRaw> {
        return try {
            val lines = csvHandler.readLines()
            lines.mapNotNull { parseLine(it) }
        } catch (_: CsvFileNotFoundException) {
            emptyList()
        }
    }

    override fun addRawVehicle(vehicle: VehicleRaw) {
        val lines = vehicle.vehicleIds.map { id ->
            "$id,${vehicle.currentHubId},${vehicle.maxCapacityKg},${vehicle.costPerKm}"
        }
        csvHandler.appendLines(lines)
    }

    fun parseLine(line: String): VehicleRaw? {
        val fields = csvHandler.splitFields(line, CSV_DELIMITER)
        if (fields.size != EXPECTED_VEHICLE_FIELDS) {
            return null
        }
        return mapFieldsToVehicle(fields)
    }

    private fun mapFieldsToVehicle(fields: List<String>): VehicleRaw? {
        val vehicleId = fields[INDEX_VEHICLE_ID]
        val currentHubId = fields[INDEX_CURRENT_HUB_ID]
        val maxCapacity = fields[INDEX_MAX_CAPACITY].toDoubleOrNull()
        val costPerKm = fields[INDEX_COST_PER_KM].toDoubleOrNull()

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
