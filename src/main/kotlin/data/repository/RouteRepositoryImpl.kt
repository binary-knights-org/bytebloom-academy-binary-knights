package data.repository

import data.local.datasource.CsvRouteDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.mapper.routes.toDomain
import data.mapper.routes.toRaw
import data.exception.NetworkUnavailableException
import data.utils.retryWithBackoff
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val remoteDataSource: RemoteRouteDataSource,
    private val localDataSource: CsvRouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private var routes: List<Route>? = null

    override suspend fun getAll(): List<Route> = routes ?: fetchRoutesFromSource().also { routes = it }

    override suspend fun getById(id: String): Route? = getAll().find { it.id == id }

    override suspend fun create(item: Route): Boolean =
        retryWithBackoff { remoteDataSource.createRawRoute(item.toRaw()) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) routes = null
            }

    override suspend fun update(item: Route): Boolean =
        retryWithBackoff { remoteDataSource.updateRawRoute(item.id, item.toRaw()) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) routes = null
            }

    override suspend fun delete(id: String): Boolean =
        retryWithBackoff { remoteDataSource.deleteRawRoute(id) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) routes = null
            }

    private suspend fun fetchRoutesFromSource(): List<Route> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }

        return retryWithBackoff {
            val remoteRawRoutes = remoteDataSource.getRawRoutes()
            remoteRawRoutes.mapNotNull { it.toDomain(warehousesById) }
        }.getOrElse { e ->
            if (e is NetworkUnavailableException) {
                println("Offline mode active: Fetching from CSV due to -> ${e.message}")
                localDataSource.getAllRoutes().mapNotNull { it.toDomain(warehousesById) }
            } else {
                throw e
            }
        }
    }
}
