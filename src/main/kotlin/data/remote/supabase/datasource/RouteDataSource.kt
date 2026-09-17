package data.remote.supabase.datasource

import data.dataholder.RouteRaw

interface RouteDataSource {
   suspend fun getRawRoutes(): List<RouteRaw>
   suspend fun createRawRoute(route: RouteRaw): Boolean
   suspend fun updateRawRoute(id: String, route: RouteRaw): Boolean
   suspend fun deleteRawRoute(id: String): Boolean
}
