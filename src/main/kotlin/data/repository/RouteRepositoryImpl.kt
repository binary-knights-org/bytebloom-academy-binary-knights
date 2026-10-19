package data.repository

import data.local.datasource.CsvRouteDataSource
import data.mapper.routes.toDomain
import data.mapper.routes.toRaw
import data.remote.datasource.RemoteRouteDataSource
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository
import java.util.logging.Logger

class RouteRepositoryImpl(
    private val remoteDataSource: RemoteRouteDataSource,
    private val localDataSource: CsvRouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : BaseRepositoryImpl<Route>("route"), RouteRepository {

    private val logger = Logger.getLogger(RouteRepositoryImpl::class.java.simpleName)

    override suspend fun getAll(): List<Route> =
        cache ?: fetchRoutesFromSource().also { cache = it }

    override suspend fun getById(id: String): Route? =
        getAll().find { it.id == id }

    override suspend fun create(item: Route): Route {
        val rawResult = executeWriteOp("create") {
            remoteDataSource.createRawRoute(item.toRaw())
        }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return rawResult.toDomain(warehousesById) ?: item
    }

    override suspend fun update(item: Route): Route {
        val rawResult = executeWriteOp("update") {
            remoteDataSource.updateRawRoute(item.id, item.toRaw())
        }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return rawResult.toDomain(warehousesById) ?: item
    }

    override suspend fun delete(id: String) {
        executeWriteOp("delete") {
            remoteDataSource.deleteRawRoute(id)
        }
    }

    private suspend fun fetchRoutesFromSource(): List<Route> =
        fetchWithFallback(
            warehouseRepository = warehouseRepository,
            remoteFetch = { remoteDataSource.getRawRoutes() },
            localFetch = { localDataSource.getAllRoutes() },
            mapToDomain = { raw, map -> raw.toDomain(map) },
            logger = logger
        )
}
