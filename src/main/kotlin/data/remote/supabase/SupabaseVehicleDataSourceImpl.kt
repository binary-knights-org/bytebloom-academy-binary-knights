package data.remote.supabase

import data.exception.DataException
import data.local.dataholder.VehicleRaw
import data.mapper.vehicles.toRaw
import data.mapper.vehicles.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.dto.vehicleDto.VehicleResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

private const val VEHICLES_TABLE = "vehicles"
private const val VEHICLES_PRIMARY_KEY = "vehicle_id"

class SupabaseVehicleDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemoteVehicleDataSource {

    override suspend fun getRawVehicles(): List<VehicleRaw> =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_FETCH, DataException.NETWORK_IO_ERROR_FETCH) {
            supabase.from(VEHICLES_TABLE)
                .select()
                .decodeList<VehicleResponseDto>()
                .map { it.toRaw() }
        }

    override suspend fun createRawVehicle(vehicle: VehicleRaw): VehicleRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_CREATE, DataException.NETWORK_IO_ERROR_CREATE) {
            supabase.from(VEHICLES_TABLE)
                .insert(vehicle.toRequestDto()) { select() }
                .decodeSingle<VehicleResponseDto>()
                .toRaw()
        }

    override suspend fun updateRawVehicle(id: String, vehicle: VehicleRaw): VehicleRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_UPDATE, DataException.NETWORK_IO_ERROR_UPDATE) {
            supabase.from(VEHICLES_TABLE)
                .update(vehicle.toRequestDto()) {
                    select()
                    filter { eq(VEHICLES_PRIMARY_KEY, id) }
                }
                .decodeSingle<VehicleResponseDto>()
                .toRaw()
        }

    override suspend fun deleteRawVehicle(id: String): Boolean =
        safeWriteCall(DataException.NETWORK_UNREACHABLE_DELETE, DataException.NETWORK_IO_ERROR_DELETE) {
            supabase.from(VEHICLES_TABLE).delete {
                filter { eq(VEHICLES_PRIMARY_KEY, id) }
            }
        }
}
