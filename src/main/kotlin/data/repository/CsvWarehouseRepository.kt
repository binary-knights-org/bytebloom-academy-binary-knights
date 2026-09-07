package data.repository

import data.mapper.toDomain
import data.processing.parser.PackageCsvParser
import data.processing.parser.RouteCsvParser
import data.processing.parser.VehicleCsvParser
import data.processing.parser.WarehouseCsvParser
import data.processing.reader.CsvFileReader
import domain.model.Warehouse
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.repository.WarehouseRepository

data class CsvFilePaths(
    val warehouses: String,
    val packages: String,
    val vehicles: String,
    val routes: String
)

class CsvWarehouseRepository(
    private val paths: CsvFilePaths,
    private val reader: CsvFileReader = CsvFileReader()
) : WarehouseRepository {

    private val parser = WarehouseCsvParser()
    private val packageParser = PackageCsvParser()
    private val vehicleParser = VehicleCsvParser()
    private val routeParser = RouteCsvParser()

    private val warehouses: List<Warehouse> by lazy {
        val rawWarehouses = reader.readLines(paths.warehouses)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> parser.parseLine(line)?.toDomain() }

        val warehousesById: Map<String, Warehouse> = rawWarehouses.associateBy { it.id }

        val packages = reader.readLines(paths.packages)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> packageParser.parseLine(line)?.toDomain(warehousesById) }

        val vehicles = reader.readLines(paths.vehicles)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> vehicleParser.parseLine(line)?.toDomain(warehousesById) }

        val routes = reader.readLines(paths.routes)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> routeParser.parseLine(line)?.toDomain(warehousesById) }

        linkWarehouseData(rawWarehouses, packages, vehicles, routes)
    }

    override fun getAllWarehouses(): List<Warehouse> {
        return warehouses
    }

    private fun linkWarehouseData(
        warehouses: List<Warehouse>,
        packages: List<Package>,
        vehicles: List<Vehicle>,
        routes: List<Route>
    ): List<Warehouse> {
        val warehouseMap = warehouses.associateBy { it.id }
        packages.forEach { pkg -> warehouseMap[pkg.originHub.id]?.addPackage(pkg) }
        vehicles.forEach { vehicle -> warehouseMap[vehicle.currentHub.id]?.addVehicle(vehicle) }
        routes.forEach { route -> warehouseMap[route.originHub.id]?.addRoute(route) }

        return warehouses
    }
}
