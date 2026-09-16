package data.remote.supabase

import data.dataholder.WarehouseRaw
import data.datasource.WarehouseDataSource
import data.mapper.warehouses.toRaw
import data.mapper.warehouses.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.warehouseDto.WarehouseResponseDto
import io.ktor.client.call.body

private const val WAREHOUSES_TABLE = "warehouses"

class SupabaseWarehouseDataSource(
    private val httpClient: SupabaseHttpClient
) : WarehouseDataSource {

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
        return response.status.value in 200..299
    }

    override suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): Boolean {
        val response = httpClient.patch(
            table = WAREHOUSES_TABLE,
            id = id,
            body = warehouse.toRequestDto()
        )
        return response.status.value in 200..299
    }

    override suspend fun deleteRawWarehouse(id: String): Boolean {
        val response = httpClient.delete(
            table = WAREHOUSES_TABLE,
            id = id
        )
        return response.status.value in 200..299
    }
}
