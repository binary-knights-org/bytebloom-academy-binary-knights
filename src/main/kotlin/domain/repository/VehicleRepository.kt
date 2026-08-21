package domain.repository

import domain.model.Vehicle

interface VehicleRepository {
    fun getAllVehicles(): List<Vehicle>
}
