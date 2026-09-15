package domain.repository

import domain.model.Route

interface RouteRepository : BaseRepository<Route, String> {

   suspend fun getAllRoutes(): List<Route>
}