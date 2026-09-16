package data.remote.supabase

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource
import data.mapper.vehicles.toRaw
import data.mapper.vehicles.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.vehicleDto.VehicleResponseDto
import io.ktor.client.call.body
import io.ktor.http.isSuccess

private const val VEHICLES_TABLE = "vehicles"

class SupabaseVehicleDataSource(
    private val httpClient: SupabaseHttpClient
) : VehicleDataSource {

    override suspend fun getRawVehicles(): List<VehicleRaw> {
        val response = httpClient.get(VEHICLES_TABLE)
        val dtos: List<VehicleResponseDto> = response.body()
        return dtos.map { it.toRaw() }
    }

    override suspend fun createRawVehicle(vehicle: VehicleRaw): Boolean {
        val response = httpClient.post(
            table = VEHICLES_TABLE,
            body = vehicle.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun updateRawVehicle(id: String, vehicle: VehicleRaw): Boolean {
        val response = httpClient.patch(
            table = VEHICLES_TABLE,
            id = id,
            body = vehicle.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun deleteRawVehicle(id: String): Boolean {
        val response = httpClient.delete(
            table = VEHICLES_TABLE,
            id = id
        )
        return response.status.isSuccess()
    }
}
