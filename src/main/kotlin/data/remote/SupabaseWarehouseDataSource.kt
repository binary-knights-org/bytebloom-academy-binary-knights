package data.remote

import data.dataholder.WarehouseRaw
import data.datasource.WarehouseDataSource
import data.mapper.toRaw
import data.remote.dto.WarehouseDto
import io.ktor.client.call.body
import kotlinx.coroutines.runBlocking

private const val WAREHOUSES_TABLE = "warehouses"

class SupabaseWarehouseDataSource(
    private val httpClient: SupabaseHttpClient
): WarehouseDataSource {

    override fun getRawWarehouses(): List<WarehouseRaw> {
        return runBlocking {
            val response = httpClient.get(WAREHOUSES_TABLE)
            val dtos: List<WarehouseDto> = response.body()
            dtos.map { it.toRaw() }
        }
    }
}
