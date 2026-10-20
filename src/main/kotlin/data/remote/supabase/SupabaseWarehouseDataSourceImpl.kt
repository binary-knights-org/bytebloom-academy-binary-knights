package data.remote.supabase

import data.exception.DataException
import data.local.dataholder.WarehouseRaw
import data.mapper.warehouses.toRaw
import data.mapper.warehouses.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.remote.dto.warehouseDto.WarehouseResponseDto
import data.remote.ratelimit.RateLimiter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

private const val WAREHOUSES_TABLE = "warehouses"
private const val WAREHOUSES_PRIMARY_KEY = "hub_id"

class SupabaseWarehouseDataSourceImpl(
    private val supabase: SupabaseClient ,
    rateLimiter: RateLimiter
) : BaseRemoteDataSource(rateLimiter), RemoteWarehouseDataSource {

    override suspend fun getRawWarehouses(): List<WarehouseRaw> =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_FETCH, DataException.NETWORK_IO_ERROR_FETCH) {
            supabase.from(WAREHOUSES_TABLE)
                .select()
                .decodeList<WarehouseResponseDto>()
                .map { it.toRaw() }
        }

    override suspend fun createRawWarehouse(warehouse: WarehouseRaw): WarehouseRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_CREATE, DataException.NETWORK_IO_ERROR_CREATE) {
            supabase.from(WAREHOUSES_TABLE)
                .insert(warehouse.toRequestDto()) { select() }
                .decodeSingle<WarehouseResponseDto>()
                .toRaw()
        }

    override suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): WarehouseRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_UPDATE, DataException.NETWORK_IO_ERROR_UPDATE) {
            supabase.from(WAREHOUSES_TABLE)
                .update(warehouse.toRequestDto()) {
                    select()
                    filter { eq(WAREHOUSES_PRIMARY_KEY, id) }
                }
                .decodeSingle<WarehouseResponseDto>()
                .toRaw()
        }

    override suspend fun deleteRawWarehouse(id: String): Boolean =
        safeWriteCall(DataException.NETWORK_UNREACHABLE_DELETE, DataException.NETWORK_IO_ERROR_DELETE) {
            supabase.from(WAREHOUSES_TABLE).delete {
                filter { eq(WAREHOUSES_PRIMARY_KEY, id) }
            }
        }
}
