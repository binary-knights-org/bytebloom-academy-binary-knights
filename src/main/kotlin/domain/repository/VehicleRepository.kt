package domain.repository

import domain.model.Vehicle

interface VehicleRepository {
  suspend  fun getAllVehicles(): List<Vehicle>
   suspend fun addVehicleToHub(vehicle: Vehicle): Boolean
}
