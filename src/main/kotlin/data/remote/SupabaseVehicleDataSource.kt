package data.remote

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource
import data.mapper.toRaw
import data.remote.dto.VehicleResponseDto
import io.ktor.client.call.body
import kotlinx.coroutines.runBlocking

private const val VEHICLES_TABLE = "vehicles"

class SupabaseVehicleDataSource(
    private val httpClient: SupabaseHttpClient
) : VehicleDataSource {

    override fun getRawVehicles(): List<VehicleRaw> = runBlocking {

        val response = httpClient.get(VEHICLES_TABLE)

        val vehicles: List<VehicleResponseDto> = response.body()

        vehicles.map { it.toRaw() }
    }

    override fun addRawVehicle(vehicle: VehicleRaw) {
        TODO("POST will be implemented next")
    }
}
