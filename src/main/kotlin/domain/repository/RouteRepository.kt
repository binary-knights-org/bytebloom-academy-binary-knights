package domain.repository

import data.dataholder.RouteRaw

interface RouteRepository {
    fun getAllRoutes(): List<RouteRaw>
}
