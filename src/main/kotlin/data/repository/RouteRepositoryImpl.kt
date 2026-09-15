package data.repository

import data.datasource.RouteDataSource
import data.mapper.routes.toDomain
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val dataSource: RouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private var routes: List<Route>? = null

    override suspend fun getAllRoutes(): List<Route> {
        routes?.let { return it }
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }
        val loadedRoutes = dataSource.getRawRoutes().mapNotNull { it.toDomain(warehousesById) }

        routes = loadedRoutes
        return loadedRoutes
    }
}
