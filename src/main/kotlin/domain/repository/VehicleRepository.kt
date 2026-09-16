package domain.repository

import domain.model.Vehicle

interface VehicleRepository {

    suspend fun create(vehicle: Vehicle): Vehicle

    suspend fun getByID(id: String): Vehicle?

    suspend fun update(vehicle: Vehicle): Vehicle

    suspend fun delete(vehicle: Vehicle): Vehicle

    suspend fun getAllVehicles(): List<Vehicle>

    suspend fun addVehicleToHub(vehicle: Vehicle): Boolean
}
