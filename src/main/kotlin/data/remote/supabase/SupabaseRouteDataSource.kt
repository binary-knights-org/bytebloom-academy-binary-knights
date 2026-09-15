package data.remote.supabase

import data.dataholder.RouteRaw
import data.datasource.RouteDataSource
import data.mapper.packages.toRaw
import data.remote.client.SupabaseHttpClient
import data.remote.dto.RouteResponseDto
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
}
