package data.repository

import data.local.datasource.CsvPackageDataSource
import data.local.datasource.CsvRouteDataSource
import data.local.datasource.CsvVehicleDataSource
import data.local.datasource.CsvWarehouseDataSource
import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.mapper.packages.toDomain
import data.mapper.routes.toDomain
import data.mapper.vehicles.toDomain
import data.mapper.warehouses.toDomain
import data.mapper.warehouses.toRaw
import data.exception.NetworkUnavailableException
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.WarehouseRepository

data class RemoteDataSources(
    val warehouse: RemoteWarehouseDataSource,
    val packageSource: RemotePackageDataSource,
    val vehicle: RemoteVehicleDataSource,
    val route: RemoteRouteDataSource
)

data class LocalDataSources(
    val warehouse: CsvWarehouseDataSource,
    val packageSource: CsvPackageDataSource,
    val vehicle: CsvVehicleDataSource,
    val route: CsvRouteDataSource
)

class WarehouseRepositoryImpl(
    private val remoteSources: RemoteDataSources,
    private val localSources: LocalDataSources
) : WarehouseRepository {

    private var warehouses: List<Warehouse>? = null

    override suspend fun getAll(): List<Warehouse> =
        warehouses ?: fetchAndLinkWarehouses().also { warehouses = it }

    override suspend fun getById(id: String): Warehouse? =
        getAll().find { it.id == id }

    override suspend fun create(item: Warehouse): Boolean =
        remoteSources.warehouse.createRawWarehouse(item.toRaw()).also { isSuccess ->
            if (isSuccess) warehouses = null
        }

    override suspend fun update(item: Warehouse): Boolean =
        remoteSources.warehouse.updateRawWarehouse(item.id, item.toRaw()).also { isSuccess ->
            if (isSuccess) warehouses = null
        }

    override suspend fun delete(id: String): Boolean =
        remoteSources.warehouse.deleteRawWarehouse(id).also { isSuccess ->
            if (isSuccess) warehouses = null
        }

    private suspend fun fetchAndLinkWarehouses(): List<Warehouse> {
        return runCatching {
            val loadedWarehouses = remoteSources.warehouse.getRawWarehouses().map { it.toDomain() }
            val warehousesById = loadedWarehouses.associateBy { it.id }

            val packages = remoteSources.packageSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
            val vehicles = remoteSources.vehicle.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }
            val routes = remoteSources.route.getRawRoutes().mapNotNull { it.toDomain(warehousesById) }

            linkWarehouseData(loadedWarehouses, packages, vehicles, routes)
        }.getOrElse { e ->
            if (e is NetworkUnavailableException) {
                println("Offline mode active: Fetching from CSV due to -> ${e.message}")

                val loadedWarehouses = localSources.warehouse.getAllWarehouses().map { it.toDomain() }
                val warehousesById = loadedWarehouses.associateBy { it.id }

                val packages = localSources.packageSource.getAllPackages().mapNotNull { it.toDomain(warehousesById) }
                val vehicles = localSources.vehicle.getAllVehicles().mapNotNull { it.toDomain(warehousesById) }
                val routes = localSources.route.getAllRoutes().mapNotNull { it.toDomain(warehousesById) }

                linkWarehouseData(loadedWarehouses, packages, vehicles, routes)
            } else {
                throw e
            }
        }
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
