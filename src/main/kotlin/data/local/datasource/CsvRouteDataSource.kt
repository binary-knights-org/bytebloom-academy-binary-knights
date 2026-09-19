package data.local.datasource

import data.local.dataholder.RouteRaw

interface CsvRouteDataSource {
    fun getAllRoutes(): List<RouteRaw>
}
