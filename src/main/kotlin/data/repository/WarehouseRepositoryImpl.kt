package data.repository

import data.datasource.PackageDataSource
import data.datasource.RouteDataSource
import data.datasource.VehicleDataSource
import data.datasource.WarehouseDataSource
import data.mapper.packages.toDomain
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val warehouseDataSource: WarehouseDataSource,
    private val packageDataSource: PackageDataSource,
    private val vehicleDataSource: VehicleDataSource,
    private val routeDataSource: RouteDataSource
) : WarehouseRepository {

    private var warehouses: List<Warehouse>? = null

    override suspend fun getAllWarehouses(): List<Warehouse> {
        warehouses?.let { return it }

        val loadedWarehouses = warehouseDataSource.getRawWarehouses().map { it.toDomain() }

        val warehousesById = loadedWarehouses.associateBy { it.id }

        val packages = packageDataSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
        val vehicles = vehicleDataSource.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }
        val routes = routeDataSource.getRawRoutes().mapNotNull { it.toDomain(warehousesById) }

        val linkedWarehouses = linkWarehouseData(loadedWarehouses, packages, vehicles, routes)
        warehouses = linkedWarehouses
        return linkedWarehouses
    }

    private fun linkWarehouseData(
        warehouses: List<Warehouse>, packages: List<Package>, vehicles: List<Vehicle>, routes: List<Route>
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