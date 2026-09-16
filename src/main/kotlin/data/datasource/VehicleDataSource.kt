package data.datasource

import data.dataholder.VehicleRaw

interface VehicleDataSource {
    suspend fun getRawVehicles(): List<VehicleRaw>
    suspend fun getRawVehicleById(id: String): VehicleRaw?
    suspend fun addRawVehicle(vehicle: VehicleRaw)
    suspend fun updateRawVehicle(vehicle: VehicleRaw)
    suspend fun deleteRawVehicle(id: String)
}
