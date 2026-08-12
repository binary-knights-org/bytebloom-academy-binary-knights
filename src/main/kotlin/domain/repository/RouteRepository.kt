package domain.repository

import data.dataholder.RouteRaw

interface RouteRepository {
    fun getRoutes(): List<RouteRaw>
}
