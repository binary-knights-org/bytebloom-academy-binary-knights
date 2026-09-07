package data.repository

import data.mapper.toDomain
import data.processing.parser.WarehouseCsvParser
import data.processing.reader.CsvFileReader
import domain.model.Warehouse
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.repository.WarehouseRepository

class CsvWarehouseRepository(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: WarehouseCsvParser = WarehouseCsvParser()
) : WarehouseRepository {

    private val warehouses: List<Warehouse> by lazy {
        reader.readLines(filePath).filter { it.isNotBlank() }.mapNotNull { line -> parser.parseLine(line)?.toDomain() }
    }

    override fun getAllWarehouses(): List<Warehouse> {
        return warehouses
    }

    override fun linkWarehouseData(
        packages: List<Package>, vehicles: List<Vehicle>, routes: List<Route>
    ): List<Warehouse> {

        val warehouses = getAllWarehouses()
        val warehouseMap = warehouses.associateBy { it.id }

        packages.forEach { pkg ->
            warehouseMap[pkg.originHub.id]?.addPackage(pkg)
        }

        vehicles.forEach { vehicle ->
            warehouseMap[vehicle.currentHub.id]?.addVehicle(vehicle)
        }

        routes.forEach { route ->
            warehouseMap[route.originHub.id]?.addRoute(route)
        }

        return warehouses
    }
}
