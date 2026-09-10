package data.local.csv

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource
import data.processing.parser.VehicleCsvParser
import data.processing.reader.CsvFileReader
import data.processing.writer.CsvFileWriter

class CsvVehicleDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: VehicleCsvParser = VehicleCsvParser()
) : VehicleDataSource {
    private val vehicleRaws = mutableListOf<VehicleRaw>()

    override fun getRawVehicles(): List<VehicleRaw> {
        val fileVehicles = reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
        return fileVehicles + vehicleRaws
    }

    override fun addRawVehicle(vehicle: VehicleRaw) {
        vehicleRaws.add(vehicle)
    }
}
