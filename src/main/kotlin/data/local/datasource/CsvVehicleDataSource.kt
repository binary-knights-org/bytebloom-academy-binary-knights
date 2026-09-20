package data.local.datasource

import data.local.dataholder.VehicleRaw

interface CsvVehicleDataSource {
    fun getAllVehicles(): List<VehicleRaw>
}
