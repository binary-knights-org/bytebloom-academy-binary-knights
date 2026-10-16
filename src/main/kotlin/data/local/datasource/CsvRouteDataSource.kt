package data.local.datasource

import data.local.dataholder.RouteRaw

interface CsvRouteDataSource {
    suspend fun getAllRoutes(): List<RouteRaw>
}
