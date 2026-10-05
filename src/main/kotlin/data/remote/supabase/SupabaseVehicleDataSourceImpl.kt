package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.VehicleRaw
import data.mapper.vehicles.toRaw
import data.mapper.vehicles.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.dto.vehicleDto.VehicleResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.util.network.UnresolvedAddressException
import java.io.IOException

private const val VEHICLES_TABLE = "vehicles"
private const val VEHICLES_PRIMARY_KEY = "vehicle_id"

class SupabaseVehicleDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemoteVehicleDataSource {

    override suspend fun getRawVehicles(): List<VehicleRaw> =
        retryWithBackoff {
            supabase
                .from(VEHICLES_TABLE)
                .select()
                .decodeList<VehicleResponseDto>()
                .map { it.toRaw() }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_FETCH
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_FETCH
                    )
                }

                else -> throw error
            }
        }

    override suspend fun createRawVehicle(
        vehicle: VehicleRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(VEHICLES_TABLE)
                    .insert(vehicle.toRequestDto())

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_CREATE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_CREATE
                    )
                }

                else -> throw error
            }
        }

    override suspend fun updateRawVehicle(
        id: String,
        vehicle: VehicleRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(VEHICLES_TABLE)
                    .update(vehicle.toRequestDto()) {
                        filter {
                            eq(VEHICLES_PRIMARY_KEY, id)
                        }
                    }

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_UPDATE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_UPDATE
                    )
                }

                else -> throw error
            }
        }

    override suspend fun deleteRawVehicle(
        id: String
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(VEHICLES_TABLE)
                    .delete {
                        filter {
                            eq(VEHICLES_PRIMARY_KEY, id)
                        }
                    }

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_DELETE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_DELETE
                    )
                }

                else -> throw error
            }
        }
}
