package domain.repository

import domain.model.Route

interface RouteRepository : BaseRepository<Route, String> {

   suspend fun getAllRoutes(): List<Route>
   suspend fun createRoute(route: Route): Boolean
   suspend fun getRouteById(id: String): Route?
   suspend fun updateRoute(route: Route): Boolean
   suspend fun deleteRoute(id: String): Boolean
}
