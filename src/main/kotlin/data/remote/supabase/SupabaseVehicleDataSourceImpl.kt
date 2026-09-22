package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.VehicleRaw
import data.remote.datasource.RemoteVehicleDataSource
import data.mapper.vehicles.toRaw
import data.mapper.vehicles.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.vehicleDto.VehicleResponseDto
import data.remote.base.BaseRemoteDataSource
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val VEHICLES_TABLE = "vehicles"

class SupabaseVehicleDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : BaseRemoteDataSource(), RemoteVehicleDataSource {

    override suspend fun getRawVehicles(): List<VehicleRaw> = retryWithBackoff {
        val response = httpClient.get(VEHICLES_TABLE)
        val dtos: List<VehicleResponseDto> = response.body()
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

    override suspend fun createRawVehicle(vehicle: VehicleRaw): Boolean = retryWithBackoff {
        val response = httpClient.post(table = VEHICLES_TABLE, body = vehicle.toRequestDto())
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

    override suspend fun updateRawVehicle(id: String, vehicle: VehicleRaw): Boolean = retryWithBackoff {
        val response =
            httpClient.patch(table = VEHICLES_TABLE, id = id, body = vehicle.toRequestDto(), primaryKey = "vehicle_id")
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

    override suspend fun deleteRawVehicle(id: String): Boolean = retryWithBackoff {
        val response = httpClient.delete(table = VEHICLES_TABLE, id = id, primaryKey = "vehicle_id")
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
