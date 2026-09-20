package data.remote.supabase

import data.local.dataholder.VehicleRaw
import data.mapper.routes.toRaw
import data.remote.datasource.RemoteVehicleDataSource
import data.mapper.vehicles.toRaw
import data.mapper.vehicles.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.routeDto.RouteResponseDto
import data.remote.dto.vehicleDto.VehicleResponseDto
import domain.exception.NetworkUnavailableException
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val VEHICLES_TABLE = "vehicles"

class SupabaseVehicleDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemoteVehicleDataSource {

    override suspend fun getRawVehicles(): List<VehicleRaw> {
        return try {
            val response = httpClient.get(VEHICLES_TABLE)
            val dtos: List<VehicleResponseDto> = response.body()
            dtos.map { it.toRaw() }
        } catch (e: UnresolvedAddressException) {
            throw NetworkUnavailableException(
                message = e.message ?: "Network unreachable - Falling back to CSV",
                cause = e
            )
        } catch (e: IOException) {
            throw NetworkUnavailableException(
                message = e.message ?: "IO Network Error - Falling back to CSV",
                cause = e
            )
        }
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
