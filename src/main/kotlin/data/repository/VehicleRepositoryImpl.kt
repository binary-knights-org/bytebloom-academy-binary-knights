package data.repository

import data.local.datasource.CsvVehicleDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.mapper.vehicles.toDomain
import data.mapper.vehicles.toRaw
import domain.model.Route
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val remoteDataSource: RemoteVehicleDataSource,
    private val localDataSource: CsvVehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    private var vehicles: List<Vehicle>? = null

    override suspend fun getAll(): List<Vehicle> =
        vehicles ?: fetchVehiclesFromSource().also { vehicles = it }

    override suspend fun getById(id: String): Vehicle? =
        getAll().find { it.id == id }

    override suspend fun create(item: Vehicle): Boolean =
        remoteDataSource.createRawVehicle(item.toRaw()).also { isSuccess ->
            if (isSuccess) vehicles = null
        }

    override suspend fun update(item: Vehicle): Boolean =
        remoteDataSource.updateRawVehicle(item.id, item.toRaw()).also { isSuccess ->
            if (isSuccess) vehicles = null
        }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.deleteRawVehicle(id).also { isSuccess ->
            if (isSuccess) vehicles = null
        }

    private suspend fun fetchVehiclesFromSource(): List<Vehicle> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }

        return try {
            val remoteRawVehicles = remoteDataSource.getRawVehicles()
            remoteRawVehicles.mapNotNull { it.toDomain(warehousesById) }

        } catch (e: Exception) {
            println("Offline mode active: Fetching from CSV due to -> ${e.message}")

            val localRawVehicle = localDataSource.getAllVehicles()
            localRawVehicle.mapNotNull { it.toDomain(warehousesById) }
        }
    }
}
