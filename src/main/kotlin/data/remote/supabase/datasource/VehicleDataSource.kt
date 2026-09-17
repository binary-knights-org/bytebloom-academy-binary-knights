package data.remote.supabase.datasource

import data.dataholder.VehicleRaw

interface VehicleDataSource {
    suspend fun getRawVehicles(): List<VehicleRaw>
    suspend fun createRawVehicle(vehicle: VehicleRaw): Boolean
    suspend fun updateRawVehicle(id: String, vehicle: VehicleRaw): Boolean
    suspend fun deleteRawVehicle(id: String): Boolean
}
