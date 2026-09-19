package data.remote.supabase

import data.local.dataholder.WarehouseRaw
import data.remote.datasource.RemoteWarehouseDataSource
import data.mapper.warehouses.toRaw
import data.mapper.warehouses.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.warehouseDto.WarehouseResponseDto
import io.ktor.client.call.body
import io.ktor.http.isSuccess

private const val WAREHOUSES_TABLE = "warehouses"

class SupabaseWarehouseDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemoteWarehouseDataSource {

    override suspend fun getRawWarehouses(): List<WarehouseRaw> {
        val response = httpClient.get(WAREHOUSES_TABLE)
        val dtos: List<WarehouseResponseDto> = response.body()
        return dtos.map { it.toRaw() }
    }

    override suspend fun createRawWarehouse(warehouse: WarehouseRaw): Boolean {
        val response = httpClient.post(
            table = WAREHOUSES_TABLE,
            body = warehouse.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): Boolean {
        val response = httpClient.patch(
            table = WAREHOUSES_TABLE,
            id = id,
            body = warehouse.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun deleteRawWarehouse(id: String): Boolean {
        val response = httpClient.delete(
            table = WAREHOUSES_TABLE,
            id = id
        )
        return response.status.isSuccess()
    }
}
