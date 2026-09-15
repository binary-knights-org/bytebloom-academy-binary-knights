package data.repository

import data.datasource.PackageDataSource
import data.datasource.RouteDataSource
import data.datasource.VehicleDataSource
import data.datasource.WarehouseDataSource
import data.mapper.packages.toDomain
import data.mapper.vehicles.toDomain
import data.mapper.routes.toDomain
import data.mapper.warehouses.toDomain
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

    override suspend fun createWarehouse(warehouse: Warehouse): Boolean {
        val currentWarehouses = getAllWarehouses().toMutableList()
        if (currentWarehouses.any { it.id == warehouse.id }) {
            return false
        }
        currentWarehouses.add(warehouse)
        warehouses = currentWarehouses
        return true
    }

    override suspend fun getWarehouseById(id: String): Warehouse? {
        return getAllWarehouses().find { it.id == id }
    }

    override suspend fun updateWarehouse(warehouse: Warehouse): Boolean {
        val currentWarehouses = getAllWarehouses().toMutableList()
        val index = currentWarehouses.indexOfFirst { it.id == warehouse.id }
        if (index == -1) {
            return false
        }
        currentWarehouses[index] = warehouse
        warehouses = currentWarehouses
        return true
    }

    override suspend fun deleteWarehouse(id: String): Boolean {
        val currentWarehouses = getAllWarehouses().toMutableList()
        val removed = currentWarehouses.removeIf { it.id == id }
        if (removed) {
            warehouses = currentWarehouses
        }
        return removed
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
