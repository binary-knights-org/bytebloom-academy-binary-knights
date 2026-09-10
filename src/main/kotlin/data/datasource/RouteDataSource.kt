package data.datasource

import data.dataholder.RouteRaw

interface RouteDataSource {
    fun getRawRoutes(): List<RouteRaw>
}
