package data.repository

import data.datasource.PackageDataSource
import data.datasource.RouteDataSource
import data.datasource.VehicleDataSource
import data.datasource.WarehouseDataSource
import data.mapper.packages.toDomain
import data.mapper.routes.toDomain
import data.mapper.vehicles.toDomain
import data.mapper.warehouses.toDomain
import data.mapper.warehouses.toRaw
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

    override suspend fun getAll(): List<Warehouse> =
        warehouses ?: fetchAndLinkWarehouses().also { warehouses = it }

    override suspend fun getById(id: String): Warehouse? =
        getAll().find { it.id == id }

    override suspend fun create(item: Warehouse): Boolean =
        warehouseDataSource.createRawWarehouse(item.toRaw()).also { isSuccess ->
            if (isSuccess) warehouses = null
        }

    override suspend fun update(item: Warehouse): Boolean =
        warehouseDataSource.updateRawWarehouse(item.id, item.toRaw()).also { isSuccess ->
            if (isSuccess) warehouses = null
        }

    override suspend fun delete(id: String): Boolean =
        warehouseDataSource.deleteRawWarehouse(id).also { isSuccess ->
            if (isSuccess) warehouses = null
        }

    private suspend fun fetchAndLinkWarehouses(): List<Warehouse> {
        val loadedWarehouses = warehouseDataSource.getRawWarehouses().map { it.toDomain() }
        val warehousesById = loadedWarehouses.associateBy { it.id }

        val packages = packageDataSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
        val vehicles = vehicleDataSource.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }
        val routes = routeDataSource.getRawRoutes().mapNotNull { it.toDomain(warehousesById) }

        return linkWarehouseData(loadedWarehouses, packages, vehicles, routes)
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
