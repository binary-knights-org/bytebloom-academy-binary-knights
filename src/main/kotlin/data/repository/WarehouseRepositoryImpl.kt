package data.repository

import data.datasource.PackageDataSource
import data.datasource.RouteDataSource
import data.datasource.VehicleDataSource
import data.datasource.WarehouseDataSource
import data.local.csv.CsvRouteDataSource
import data.local.csv.CsvVehicleDataSource
import data.local.csv.CsvWarehouseDataSource
import data.local.csv.CsvPackageDataSource
import data.mapper.toDomain
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val warehouseFilePath: String,
    private val packageFilePath: String,
    private val vehicleFilePath: String,
    private val routeFilePath: String
) : WarehouseRepository {

    private val warehouseDataSource: WarehouseDataSource =
        CsvWarehouseDataSource(warehouseFilePath)

    private val packageDataSource: PackageDataSource =
        CsvPackageDataSource(packageFilePath)

    private val vehicleDataSource: VehicleDataSource =
        CsvVehicleDataSource(vehicleFilePath)

    private val routeDataSource: RouteDataSource =
        CsvRouteDataSource(routeFilePath)

    private val warehouses: List<Warehouse> by lazy {
        val warehouses = warehouseDataSource
            .getRawWarehouses()
            .map { it.toDomain() }

        val warehousesById = warehouses.associateBy { it.id }

        val packages = packageDataSource
            .getRawPackages()
            .mapNotNull { it.toDomain(warehousesById) }

        val vehicles = vehicleDataSource
            .getRawVehicles()
            .mapNotNull { it.toDomain(warehousesById) }

        val routes = routeDataSource
            .getRawRoutes()
            .mapNotNull { it.toDomain(warehousesById) }

        linkWarehouseData(warehouses, packages, vehicles, routes)
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
