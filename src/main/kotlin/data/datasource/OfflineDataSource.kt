package data.datasource

import data.dataholder.PackageRaw
import data.dataholder.RouteRaw
import data.dataholder.VehicleRaw
import data.dataholder.WarehouseRaw
import data.processing.parser.PackageCsvParser
import data.processing.parser.RouteCsvParser
import data.processing.parser.VehicleCsvParser
import data.processing.parser.WarehouseCsvParser
import data.processing.reader.CsvFileReader

class CsvWarehouseDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: WarehouseCsvParser = WarehouseCsvParser()
) : WarehouseDataSource {
    override fun getRawWarehouses(): List<WarehouseRaw> {
        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
    }
}

class CsvPackageDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: PackageCsvParser = PackageCsvParser()
) : PackageDataSource {
    override fun getRawPackages(): List<PackageRaw> {
        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
    }
}

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

class CsvRouteDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: RouteCsvParser = RouteCsvParser()
) : RouteDataSource {
    override fun getRawRoutes(): List<RouteRaw> {
        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
    }
}
