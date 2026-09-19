package data.remote.supabase

import data.local.dataholder.RouteRaw
import data.mapper.packages.toRaw
import data.remote.datasource.RemoteRouteDataSource
import data.mapper.routes.toRaw
import data.mapper.routes.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.packageDto.PackageResponseDto
import data.remote.dto.routeDto.RouteResponseDto
import domain.exception.NetworkUnavailableException
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val ROUTES_TABLE = "routes"

class SupabaseRouteDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemoteRouteDataSource {

    override suspend fun getRawRoutes(): List<RouteRaw> {
        return try {
            val response = httpClient.get(ROUTES_TABLE)
            val dtos: List<RouteResponseDto> = response.body()
            dtos.map { it.toRaw() }
        } catch (e: UnresolvedAddressException) {
            throw NetworkUnavailableException(
                message = e.message ?: "Network unreachable - Falling back to CSV",
                cause = e
            )
        } catch (e: IOException) {
            throw NetworkUnavailableException(
                message = e.message ?: "IO Network Error - Falling back to CSV",
                cause = e
            )
        }
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
