package data.repository

import data.dataholder.VehicleRaw
import data.mapper.toDomain
import data.reader.CsvFileReader
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

private const val EXPECTED_VEHICLE_FIELDS = 4
private const val CSV_DELIMITER = ","

private const val INDEX_VEHICLE_ID = 0
private const val INDEX_CURRENT_HUB_ID = 1
private const val INDEX_MAX_CAPACITY = 2
private const val INDEX_COST_PER_KM = 3

class CsvVehicleRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository,
    private val reader: CsvFileReader = CsvFileReader()
) : VehicleRepository {

    override fun getAllVehicles(): List<Vehicle> {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }
        val lines = reader.readLines(filePath)
        return extractVehicles(lines, warehousesById)
    }

    private fun extractVehicles(
        lines: List<String>,
        warehousesById: Map<String, Warehouse>
    ): List<Vehicle> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it)?.toDomain(warehousesById) }
    }

    private fun parseLine(line: String): VehicleRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_VEHICLE_FIELDS)) {
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
}
