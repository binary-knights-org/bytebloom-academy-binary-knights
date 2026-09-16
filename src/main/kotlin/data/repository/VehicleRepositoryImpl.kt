package data.repository

import data.dataholder.VehicleRaw
import data.datasource.VehicleDataSource
import data.mapper.vehicles.toDomain
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import domain.exception.VehicleException

class VehicleRepositoryImpl(
    private val dataSource: VehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    private var vehicles: List<Vehicle>? = null

    override suspend fun getAllVehicles(): List<Vehicle> {
        vehicles?.let { return it }
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }
        val loadedVehicles = dataSource.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }

        vehicles = loadedVehicles
        return loadedVehicles
    }

    override suspend fun addVehicleToHub(vehicle: Vehicle): Boolean {
        val vehicleExists = dataSource.getRawVehicles().flatMap { it.vehicleIds }.any { it == vehicle.id }
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

    override suspend fun create(vehicle: Vehicle): Vehicle {
        val newVehicle = VehicleRaw(
            vehicleIds = listOf(vehicle.id),
            currentHubId = vehicle.currentHub.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )

        dataSource.addRawVehicle(newVehicle)

        return vehicle
    }
    override suspend fun update(vehicle: Vehicle): Vehicle {
        val existingVehicle = dataSource
            .getRawVehicles()
            .firstOrNull { it.vehicleIds.contains(vehicle.id) }

        if (existingVehicle != null) {
            val updatedVehicle = VehicleRaw(
                vehicleIds = existingVehicle.vehicleIds,
                currentHubId = vehicle.currentHub.id,
                maxCapacityKg = vehicle.maxCapacityKg,
                costPerKm = vehicle.costPerKm
            )

            dataSource.updateRawVehicle(updatedVehicle)
        }

        return vehicle
    }

    override suspend fun getByID(id: String): Vehicle? {
        val rawVehicle = dataSource
            .getRawVehicleById(id)

        val warehouseById = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return rawVehicle?.toDomain(warehouseById)

    }
    override suspend fun delete(vehicle: Vehicle): Vehicle {
        dataSource.deleteRawVehicle(vehicle.id)

        return vehicle
    }
}
