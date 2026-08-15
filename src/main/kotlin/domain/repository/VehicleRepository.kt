package domain.repository

import data.dataholder.VehicleRaw

interface VehicleRepository {
    fun getAllVehicles(): List<VehicleRaw>
}
