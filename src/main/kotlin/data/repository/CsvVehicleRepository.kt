package data.repository

import data.dataholder.VehicleRaw
import data.mapper.toDomain
import data.processing.parser.VehicleCsvParser
import data.processing.reader.CsvFileReader
import data.processing.writer.CsvFileWriter
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class CsvVehicleRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository,
    private val reader: CsvFileReader = CsvFileReader(),
    private val writer: CsvFileWriter = CsvFileWriter(),
    private val parser: VehicleCsvParser = VehicleCsvParser()
) : VehicleRepository {

    override fun getAllVehicles(): List<Vehicle> {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }

        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> parser.parseLine(line)?.toDomain(warehousesById) }
    }

    override fun addVehicleToHub(vehicle: Vehicle): Boolean {
        val rawVehicles = reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> parser.parseLine(line) }

        val vehicleExists = rawVehicles.flatMap { it.vehicleIds }.any { it == vehicle.id }

        if (vehicleExists) {
            return false
        }

        val newVehicle = VehicleRaw(
            vehicleIds = listOf(vehicle.id),
            currentHubId = vehicle.currentHub.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )

        writer.writeVehicles(filePath, rawVehicles + newVehicle)
        return true
    }
}
