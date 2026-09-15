package domain.repository

import domain.model.Route

interface RouteRepository {
   suspend fun getAllRoutes(): List<Route>
}
