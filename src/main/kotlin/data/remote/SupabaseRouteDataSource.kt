package data.remote

import data.dataholder.RouteRaw
import data.datasource.RouteDataSource
import data.mapper.toRaw
import data.remote.dto.RouteDto
import io.ktor.client.call.body
import kotlinx.coroutines.runBlocking

private const val ROUTES_TABLE = "routes"

class SupabaseRouteDataSource(
    private val httpClient: SupabaseHttpClient
) : RouteDataSource {

    override fun getRawRoutes(): List<RouteRaw> {
        return runBlocking {
            val response = httpClient.get(ROUTES_TABLE)
            val dtos: List<RouteDto> = response.body()
            dtos.map { it.toRaw() }
        }
    }
}
