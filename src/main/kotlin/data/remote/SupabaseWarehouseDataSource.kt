package data.remote
import data.dataholder.WarehouseRaw
import data.datasource.WarehouseDataSource
import data.mapper.toRaw
import data.remote.dto.WarehouseRequestDto
import data.remote.dto.WarehouseResponseDto
import io.ktor.client.call.body
private const val WAREHOUSES_TABLE = "warehouses"

class SupabaseWarehouseDataSource(
    private val httpClient: SupabaseHttpClient
): WarehouseDataSource {

    override suspend fun getRawWarehouses(): List<WarehouseRaw> {
        val response = httpClient.get(WAREHOUSES_TABLE)
        val dtos: List<WarehouseResponseDto> = response.body()

        return dtos.map { it.toRaw() }
    }
}
