package data.datasource

import data.dataholder.VehicleRaw

interface VehicleDataSource {
    suspend fun getRawVehicles(): List<VehicleRaw>
   suspend fun addRawVehicle(vehicle: VehicleRaw)

}
