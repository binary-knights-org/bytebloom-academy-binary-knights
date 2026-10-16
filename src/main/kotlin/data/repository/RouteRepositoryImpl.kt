package data.repository

import data.exception.NetworkUnavailableException
import data.exception.translateDataError
import data.local.datasource.CsvRouteDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.mapper.routes.toDomain
import data.mapper.routes.toRaw
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.logging.Logger

class RouteRepositoryImpl(
    private val remoteDataSource: RemoteRouteDataSource,
    private val localDataSource: CsvRouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private val logger = Logger.getLogger(PackageRepositoryImpl::class.java.simpleName)
    private var routes: List<Route>? = null

    override suspend fun getAll(): List<Route> =
        routes ?: fetchRoutesFromSource().also { routes = it }

    override suspend fun getById(id: String): Route? =
        getAll().find { it.id == id }

    override suspend fun create(item: Route): Boolean =
        runCatching {
            remoteDataSource.createRawRoute(item.toRaw())
        }.fold(
            onSuccess = { isCreated ->
                if (isCreated) routes = null
                isCreated
            },
            onFailure = { error ->
                throw translateDataError(error, "create", "route")
            }
        )

    override suspend fun update(item: Route): Boolean =
        runCatching {
            remoteDataSource.updateRawRoute(item.id, item.toRaw())
        }.fold(
            onSuccess = { isUpdated ->
                if (isUpdated) routes = null
                isUpdated
            },
            onFailure = { error ->
                throw translateDataError(error, "update", "route")
            }
        )

    override suspend fun delete(id: String): Boolean =
        runCatching {
            remoteDataSource.deleteRawRoute(id)
        }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) routes = null
                isDeleted
            },
            onFailure = { error ->
                throw translateDataError(error, "delete", "route")
            }
        )

    private suspend fun fetchRoutesFromSource(): List<Route> = coroutineScope {
        val warehousesById = async { warehouseRepository.getAll().associateBy { it.id } }
        val remoteResult = runCatching {
            remoteDataSource.getRawRoutes()
        }

        remoteResult
            .map { raw -> raw.mapNotNull { it.toDomain(warehousesById.await()) } }
            .getOrElse { error ->
                if (error is NetworkUnavailableException) {
                    logger.warning("Remote unavailable, falling back to local CSV for routes")
                    localDataSource
                        .getAllRoutes()
                        .mapNotNull { it.toDomain(warehousesById.await()) }
                } else {
                    throw translateDataError(error, "fetch", "routes")
                }
            }
    }
}
