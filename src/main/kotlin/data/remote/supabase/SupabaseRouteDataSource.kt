package data.remote.supabase

import data.dataholder.RouteRaw
import data.datasource.RouteDataSource
import data.mapper.routes.toRaw
import data.mapper.routes.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.routeDto.RouteResponseDto
import io.ktor.client.call.body

private const val ROUTES_TABLE = "routes"

class SupabaseRouteDataSource(
    private val httpClient: SupabaseHttpClient
) : RouteDataSource {

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
        return response.status.value in 200..299
    }

    override suspend fun updateRawRoute(id: String, route: RouteRaw): Boolean {
        val response = httpClient.patch(
            table = ROUTES_TABLE,
            id = id,
            body = route.toRequestDto()
        )
        return response.status.value in 200..299
    }

    override suspend fun deleteRawRoute(id: String): Boolean {
        val response = httpClient.delete(
            table = ROUTES_TABLE,
            id = id
        )
        return response.status.value in 200..299
    }
}
