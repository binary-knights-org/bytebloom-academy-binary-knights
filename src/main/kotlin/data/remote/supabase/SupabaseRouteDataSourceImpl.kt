package data.remote.supabase

import data.exception.DataException
import data.local.dataholder.RouteRaw
import data.mapper.routes.toRaw
import data.mapper.routes.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.dto.routeDto.RouteResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

private const val ROUTES_TABLE = "routes"
private const val ROUTES_PRIMARY_KEY = "route_id"

class SupabaseRouteDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemoteRouteDataSource {

    override suspend fun getRawRoutes(): List<RouteRaw> =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_FETCH, DataException.NETWORK_IO_ERROR_FETCH) {
            supabase.from(ROUTES_TABLE)
                .select()
                .decodeList<RouteResponseDto>()
                .map { it.toRaw() }
        }

    override suspend fun createRawRoute(route: RouteRaw): RouteRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_CREATE, DataException.NETWORK_IO_ERROR_CREATE) {
            supabase.from(ROUTES_TABLE)
                .insert(route.toRequestDto()) { select() }
                .decodeSingle<RouteResponseDto>()
                .toRaw()
        }

    override suspend fun updateRawRoute(id: String, route: RouteRaw): RouteRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_UPDATE, DataException.NETWORK_IO_ERROR_UPDATE) {
            supabase.from(ROUTES_TABLE)
                .update(route.toRequestDto()) {
                    select()
                    filter { eq(ROUTES_PRIMARY_KEY, id) }
                }
                .decodeSingle<RouteResponseDto>()
                .toRaw()
        }

    override suspend fun deleteRawRoute(id: String): Boolean =
        safeWriteCall(DataException.NETWORK_UNREACHABLE_DELETE, DataException.NETWORK_IO_ERROR_DELETE) {
            supabase.from(ROUTES_TABLE).delete {
                filter { eq(ROUTES_PRIMARY_KEY, id) }
            }
        }
}
