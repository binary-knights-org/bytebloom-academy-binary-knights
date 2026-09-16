package data.repository

import data.datasource.RouteDataSource
import data.mapper.routes.toDomain
import data.mapper.routes.toRaw
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val dataSource: RouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private var routes: List<Route>? = null

    override suspend fun getAll(): List<Route> =
        routes ?: fetchRoutesFromSource().also { routes = it }

    override suspend fun getById(id: String): Route? =
        getAll().find { it.id == id }

    override suspend fun create(item: Route): Boolean =
        dataSource.createRawRoute(item.toRaw()).also { isSuccess ->
            if (isSuccess) routes = null
        }

    override suspend fun update(item: Route): Boolean =
        dataSource.updateRawRoute(item.id, item.toRaw()).also { isSuccess ->
            if (isSuccess) routes = null
        }

    override suspend fun delete(id: String): Boolean =
        dataSource.deleteRawRoute(id).also { isSuccess ->
            if (isSuccess) routes = null
        }

    private suspend fun fetchRoutesFromSource(): List<Route> =
        warehouseRepository.getAll()
            .associateBy { it.id }
            .let { warehousesById ->
                dataSource.getRawRoutes().mapNotNull { it.toDomain(warehousesById) }
            }
}
