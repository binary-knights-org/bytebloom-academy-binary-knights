package data.datasource

import data.dataholder.VehicleRaw

interface VehicleDataSource {
    fun getRawVehicles(): List<VehicleRaw>
    fun addRawVehicle(vehicle: VehicleRaw)

}
