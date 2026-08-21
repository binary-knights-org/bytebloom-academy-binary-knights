package domain.repository

import domain.model.Route

interface RouteRepository {
    fun getAllRoutes(): List<Route>
}
