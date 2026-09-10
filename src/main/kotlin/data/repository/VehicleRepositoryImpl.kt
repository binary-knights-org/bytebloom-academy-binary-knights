package data.repository

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource
import data.mapper.toDomain
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val dataSource: VehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {
    override fun getAllVehicles(): List<Vehicle> {
        val warehousesById = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }
        return dataSource
            .getRawVehicles()
            .mapNotNull { it.toDomain(warehousesById) }
    }
    override fun addVehicleToHub(vehicle: Vehicle): Boolean {
        val vehicleExists = dataSource.getRawVehicles()
            .flatMap { it.vehicleIds }
            .any { it == vehicle.id }
        if (vehicleExists) {
            return false
        }
        val newVehicle = VehicleRaw(
            vehicleIds = listOf(vehicle.id),
            currentHubId = vehicle.currentHub.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )
        dataSource.addRawVehicle(newVehicle)
        return true
    }
}
