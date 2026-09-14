package data.datasource

import data.dataholder.WarehouseRaw
import data.mapper.toRaw
import data.remote.SupabaseHttpClient
import data.remote.dto.WarehouseDto
import io.ktor.client.call.body
import kotlinx.coroutines.runBlocking

private const val WAREHOUSES_TABLE = "warehouses"

class SupabaseWarehouseDataSource : WarehouseDataSource {

    override fun getRawWarehouses(): List<WarehouseRaw> {
        return runBlocking {
            val response = SupabaseHttpClient.get(WAREHOUSES_TABLE)
            val dtos: List<WarehouseDto> = response.body()
            dtos.map { it.toRaw() }
        }
    }
}
