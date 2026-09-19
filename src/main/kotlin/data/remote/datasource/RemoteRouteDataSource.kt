package data.remote.datasource

import data.local.dataholder.RouteRaw

interface RemoteRouteDataSource {
   suspend fun getRawRoutes(): List<RouteRaw>
   suspend fun createRawRoute(route: RouteRaw): Boolean
   suspend fun updateRawRoute(id: String, route: RouteRaw): Boolean
   suspend fun deleteRawRoute(id: String): Boolean
}
