package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.RouteRaw
import data.remote.datasource.RemoteRouteDataSource
import data.mapper.routes.toRaw
import data.mapper.routes.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.routeDto.RouteResponseDto
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val ROUTES_TABLE = "routes"

class SupabaseRouteDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemoteRouteDataSource {

    override suspend fun getRawRoutes(): List<RouteRaw> = runCatching {
        val response = httpClient.get(ROUTES_TABLE)
        val dtos: List<RouteResponseDto> = response.body()
        dtos.map { it.toRaw() }
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_FETCH
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_FETCH)
            else -> throw e
        }
    }

    override suspend fun createRawRoute(route: RouteRaw): Boolean = runCatching {
        val response = httpClient.post(table = ROUTES_TABLE, body = route.toRequestDto())
        response.status.isSuccess()
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_CREATE
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_CREATE)
            else -> throw e
        }
    }

    override suspend fun updateRawRoute(id: String, route: RouteRaw): Boolean = runCatching {
        val response =
            httpClient.patch(table = ROUTES_TABLE, id = id, body = route.toRequestDto(), primaryKey = "route_id")
        response.status.isSuccess()
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_UPDATE
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_UPDATE)
            else -> throw e
        }
    }

    override suspend fun deleteRawRoute(id: String): Boolean = runCatching {
        val response = httpClient.delete(table = ROUTES_TABLE, id = id, primaryKey = "route_id")
        response.status.isSuccess()
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_DELETE
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_DELETE)
            else -> throw e
        }
    }
}
