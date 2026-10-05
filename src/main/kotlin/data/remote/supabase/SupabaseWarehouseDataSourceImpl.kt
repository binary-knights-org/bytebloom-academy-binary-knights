package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.WarehouseRaw
import data.mapper.warehouses.toRaw
import data.mapper.warehouses.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.remote.dto.warehouseDto.WarehouseResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.util.network.UnresolvedAddressException
import java.io.IOException

private const val WAREHOUSES_TABLE = "warehouses"
private const val WAREHOUSES_PRIMARY_KEY = "hub_id"

class SupabaseWarehouseDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemoteWarehouseDataSource {

    override suspend fun getRawWarehouses(): List<WarehouseRaw> =
        retryWithBackoff {
            supabase
                .from(WAREHOUSES_TABLE)
                .select()
                .decodeList<WarehouseResponseDto>()
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

    override suspend fun createRawWarehouse(
        warehouse: WarehouseRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(WAREHOUSES_TABLE)
                    .insert(warehouse.toRequestDto())

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

    override suspend fun updateRawWarehouse(
        id: String,
        warehouse: WarehouseRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(WAREHOUSES_TABLE)
                    .update(warehouse.toRequestDto()) {
                        filter {
                            eq(WAREHOUSES_PRIMARY_KEY, id)
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

    override suspend fun deleteRawWarehouse(
        id: String
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(WAREHOUSES_TABLE)
                    .delete {
                        filter {
                            eq(WAREHOUSES_PRIMARY_KEY, id)
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
