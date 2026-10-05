package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.RouteRaw
import data.mapper.routes.toRaw
import data.mapper.routes.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.dto.routeDto.RouteResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.util.network.UnresolvedAddressException
import java.io.IOException

private const val ROUTES_TABLE = "routes"
private const val ROUTES_PRIMARY_KEY = "route_id"

class SupabaseRouteDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemoteRouteDataSource {

    override suspend fun getRawRoutes(): List<RouteRaw> =
        retryWithBackoff {
            supabase
                .from(ROUTES_TABLE)
                .select()
                .decodeList<RouteResponseDto>()
                .map { it.toRaw() }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_FETCH
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_FETCH
                    )
                }

                else -> throw error
            }
        }

    override suspend fun createRawRoute(
        route: RouteRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(ROUTES_TABLE)
                    .insert(route.toRequestDto())

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_CREATE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_CREATE
                    )
                }

                else -> throw error
            }
        }

    override suspend fun updateRawRoute(
        id: String,
        route: RouteRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(ROUTES_TABLE)
                    .update(route.toRequestDto()) {
                        filter {
                            eq(ROUTES_PRIMARY_KEY, id)
                        }
                    }

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_UPDATE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_UPDATE
                    )
                }

                else -> throw error
            }
        }

    override suspend fun deleteRawRoute(
        id: String
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(ROUTES_TABLE)
                    .delete {
                        filter {
                            eq(ROUTES_PRIMARY_KEY, id)
                        }
                    }

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_DELETE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_DELETE
                    )
                }

                else -> throw error
            }
        }
}
