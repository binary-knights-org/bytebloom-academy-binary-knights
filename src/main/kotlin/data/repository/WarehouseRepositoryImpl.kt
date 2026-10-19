package data.repository

import data.exception.NetworkUnavailableException
import data.exception.translateDataError
import data.local.datasource.CsvPackageDataSource
import data.local.datasource.CsvRouteDataSource
import data.local.datasource.CsvVehicleDataSource
import data.local.datasource.CsvWarehouseDataSource
import data.mapper.packages.toDomain
import data.mapper.routes.toDomain
import data.mapper.vehicles.toDomain
import data.mapper.warehouses.toDomain
import data.mapper.warehouses.toRaw
import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.logging.Logger

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
) : BaseRepositoryImpl<Warehouse>("warehouse"), WarehouseRepository {

    private val logger = Logger.getLogger(WarehouseRepositoryImpl::class.java.simpleName)

    override suspend fun getAll(): List<Warehouse> =
        cache ?: fetchAndLinkWarehouses().also { cache = it }

    override suspend fun getById(id: String): Warehouse? =
        getAll().find { it.id == id }

    override suspend fun create(item: Warehouse): Warehouse {
        val rawResult = executeWriteOp("create") {
            remoteSources.warehouse.createRawWarehouse(item.toRaw())
        }
        return rawResult.toDomain()
    }

    override suspend fun update(item: Warehouse): Warehouse {
        val rawResult = executeWriteOp("update") {
            remoteSources.warehouse.updateRawWarehouse(item.id, item.toRaw())
        }
        return rawResult.toDomain()
    }

    override suspend fun delete(id: String) {
        executeWriteOp("delete") {
            remoteSources.warehouse.deleteRawWarehouse(id)
        }
    }

    private suspend fun fetchAndLinkWarehouses(): List<Warehouse> =
        runCatching {
            loadFromRemote()
        }.getOrElse { error ->
            if (error is NetworkUnavailableException) {
                loadFromLocal()
            } else {
                throw translateDataError(error, "fetch", "warehouse data")
            }
        }

    private suspend fun loadFromRemote(): List<Warehouse> = coroutineScope {
        val loadedWarehouses = remoteSources.warehouse.getRawWarehouses().map { it.toDomain() }
        val warehousesById = loadedWarehouses.associateBy { it.id }

        val packages = async {
            remoteSources.packageSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
        }

        val vehicles = async {
            remoteSources.vehicle.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }
        }

        val routes = async {
            remoteSources.route.getRawRoutes().mapNotNull { it.toDomain(warehousesById) }
        }

        linkWarehouseData(
            loadedWarehouses,
            packages.await(),
            vehicles.await(),
            routes.await()
        )
    }

    private suspend fun loadFromLocal(): List<Warehouse> = coroutineScope {
        logger.warning("Remote unavailable, falling back to local CSV for warehouses")

        val loadedWarehouses = localSources.warehouse.getAllWarehouses().map { it.toDomain() }
        val warehousesById = loadedWarehouses.associateBy { it.id }

        val packages = async {
            localSources.packageSource.getAllPackages().mapNotNull { it.toDomain(warehousesById) }
        }

        val vehicles = async {
            localSources.vehicle.getAllVehicles().mapNotNull { it.toDomain(warehousesById) }
        }

        val routes = async {
            localSources.route.getAllRoutes().mapNotNull { it.toDomain(warehousesById) }
        }

        linkWarehouseData(
            loadedWarehouses,
            packages.await(),
            vehicles.await(),
            routes.await()
        )
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
