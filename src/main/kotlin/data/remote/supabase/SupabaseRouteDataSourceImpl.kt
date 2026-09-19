package data.remote.supabase

import data.local.dataholder.RouteRaw
import data.remote.datasource.RemoteRouteDataSource
import data.mapper.routes.toRaw
import data.mapper.routes.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.routeDto.RouteResponseDto
import io.ktor.client.call.body
import io.ktor.http.isSuccess

private const val ROUTES_TABLE = "routes"

class SupabaseRouteDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemoteRouteDataSource {

    override suspend fun getRawRoutes(): List<RouteRaw> {
        val response = httpClient.get(ROUTES_TABLE)
        val dtos: List<RouteResponseDto> = response.body()
        return dtos.map { it.toRaw() }
    }

    override suspend fun createRawRoute(route: RouteRaw): Boolean {
        val response = httpClient.post(
            table = ROUTES_TABLE,
            body = route.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun updateRawRoute(id: String, route: RouteRaw): Boolean {
        val response = httpClient.patch(
            table = ROUTES_TABLE,
            id = id,
            body = route.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun deleteRawRoute(id: String): Boolean {
        val response = httpClient.delete(
            table = ROUTES_TABLE,
            id = id
        )
        return response.status.isSuccess()
    }
}
