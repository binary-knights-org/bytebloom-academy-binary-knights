package data.local.datasource

import data.local.dataholder.VehicleRaw

interface CsvVehicleDataSource {
    suspend fun getAllVehicles(): List<VehicleRaw>
}
