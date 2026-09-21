package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.WarehouseRaw
import data.remote.datasource.RemoteWarehouseDataSource
import data.mapper.warehouses.toRaw
import data.mapper.warehouses.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.warehouseDto.WarehouseResponseDto
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val WAREHOUSES_TABLE = "warehouses"

class SupabaseWarehouseDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemoteWarehouseDataSource {

    override suspend fun getRawWarehouses(): List<WarehouseRaw> = runCatching {
        val response = httpClient.get(WAREHOUSES_TABLE)
        val dtos: List<WarehouseResponseDto> = response.body()
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

    override suspend fun createRawWarehouse(warehouse: WarehouseRaw): Boolean = runCatching {
        val response = httpClient.post(table = WAREHOUSES_TABLE, body = warehouse.toRequestDto())
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

    override suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): Boolean = runCatching {
        val response = httpClient.patch(
            table = WAREHOUSES_TABLE,
            id = id,
            body = warehouse.toRequestDto(),
            primaryKey = "warehouse_id"
        )
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

    override suspend fun deleteRawWarehouse(id: String): Boolean = runCatching {
        val response = httpClient.delete(table = WAREHOUSES_TABLE, id = id, primaryKey = "warehouse_id")
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
