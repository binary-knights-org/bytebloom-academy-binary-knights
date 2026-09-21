package data.repository

import data.local.datasource.CsvVehicleDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.mapper.vehicles.toDomain
import data.mapper.vehicles.toRaw
import data.exception.NetworkUnavailableException
import data.mapper.packages.toDomain
import data.utils.retryWithBackoff
import domain.model.Package
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
        retryWithBackoff { remoteDataSource.createRawVehicle(item.toRaw()) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) vehicles = null
            }

    override suspend fun update(item: Vehicle): Boolean =
        retryWithBackoff { remoteDataSource.updateRawVehicle(item.id, item.toRaw()) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) vehicles = null
            }

    override suspend fun delete(id: String): Boolean =
        retryWithBackoff { remoteDataSource.deleteRawVehicle(id) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) vehicles = null
            }

    private suspend fun fetchVehiclesFromSource(): List<Vehicle> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }

        return retryWithBackoff {
            remoteDataSource.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }
        }.getOrElse { e ->
            if (e is NetworkUnavailableException) {
                println("Offline mode active: Fetching from CSV due to -> ${e.message}")
                localDataSource.getAllVehicles().mapNotNull { it.toDomain(warehousesById) }
            } else {
                throw e
            }
        }
    }
}
