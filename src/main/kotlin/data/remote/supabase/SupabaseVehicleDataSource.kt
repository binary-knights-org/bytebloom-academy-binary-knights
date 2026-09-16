package data.remote.supabase

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource
import data.mapper.vehicles.toRaw
import data.remote.dto.VehicleResponseDto
import io.ktor.client.call.body
import data.mapper.vehicles.toRequestDto
import data.remote.client.SupabaseHttpClient

private const val VEHICLES_TABLE = "vehicles"

class SupabaseVehicleDataSource(
    private val httpClient: SupabaseHttpClient
) : VehicleDataSource {

    override suspend fun getRawVehicles(): List<VehicleRaw> {

        val response = httpClient.get(VEHICLES_TABLE)

        val vehicles: List<VehicleResponseDto> = response.body()

        return vehicles.map { it.toRaw() }
    }

    override suspend fun addRawVehicle(vehicle: VehicleRaw) {
        val request = vehicle.toRequestDto()
        httpClient.post(
            VEHICLES_TABLE,
            request
        )
    }

    override suspend fun updateRawVehicle(vehicle: VehicleRaw) {
        val request = vehicle.toRequestDto()
        httpClient.patch(
            VEHICLES_TABLE,
            vehicle.vehicleIds.first(),
            request
        )
    }

    override suspend fun getRawVehicleById(id: String): VehicleRaw? {
        val response = httpClient.get(
            "$VEHICLES_TABLE?vehicle_id=eq.$id"
        )
        val vehicles: List<VehicleResponseDto> = response.body()

        return vehicles.firstOrNull()?.toRaw()
    }
    override suspend fun deleteRawVehicle(id: String) {
        httpClient.delete(
            VEHICLES_TABLE,
            id
        )
    }
}
