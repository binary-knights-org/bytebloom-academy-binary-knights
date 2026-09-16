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

    override suspend fun createRoute(route: Route): Boolean {
        val currentRoutes = getAllRoutes().toMutableList()
        if (currentRoutes.any { it.id == route.id }) {
            return false
        }
        currentRoutes.add(route)
        routes = currentRoutes
        return true
    }

    override suspend fun getRouteById(id: String): Route? {
        return getAllRoutes().find { it.id == id }
    }

    override suspend fun updateRoute(route: Route): Boolean {
        val currentRoutes = getAllRoutes().toMutableList()
        val index = currentRoutes.indexOfFirst { it.id == route.id }
        if (index == -1) {
            return false
        }
        currentRoutes[index] = route
        routes = currentRoutes
        return true
    }

    override suspend fun deleteRoute(id: String): Boolean {
        val currentRoutes = getAllRoutes().toMutableList()
        val removed = currentRoutes.removeIf { it.id == id }
        if (removed) {
            routes = currentRoutes
        }
        return removed
    }

}
