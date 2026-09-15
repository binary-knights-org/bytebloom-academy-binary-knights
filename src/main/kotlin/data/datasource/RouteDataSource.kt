package data.datasource

import data.dataholder.RouteRaw

interface RouteDataSource {
   suspend fun getRawRoutes(): List<RouteRaw>
}
