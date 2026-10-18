package data.remote.datasource

import data.local.dataholder.VehicleRaw

interface RemoteVehicleDataSource {
    suspend fun getRawVehicles(): List<VehicleRaw>
    suspend fun createRawVehicle(vehicle: VehicleRaw): VehicleRaw
    suspend fun updateRawVehicle(id: String, vehicle: VehicleRaw): VehicleRaw
    suspend fun deleteRawVehicle(id: String): Boolean
}
