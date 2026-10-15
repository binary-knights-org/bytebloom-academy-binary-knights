package data.repository

import data.exception.NetworkUnavailableException
import data.exception.translateDataError
import data.local.datasource.CsvVehicleDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.mapper.vehicles.toDomain
import data.mapper.vehicles.toRaw
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
        runCatching {
            remoteDataSource.createRawVehicle(item.toRaw())
        }.fold(
            onSuccess = { isCreated ->
                if (isCreated) vehicles = null
                isCreated
            },
            onFailure = { error ->
                throw translateDataError(error, "create", "vehicle")
            }
        )

    override suspend fun update(item: Vehicle): Boolean =
        runCatching {
            remoteDataSource.updateRawVehicle(item.id, item.toRaw())
        }.fold(
            onSuccess = { isUpdated ->
                if (isUpdated) vehicles = null
                isUpdated
            },
            onFailure = { error ->
                throw translateDataError(error, "update", "vehicle")
            }
        )

    override suspend fun delete(id: String): Boolean =
        runCatching {
            remoteDataSource.deleteRawVehicle(id)
        }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) vehicles = null
                isDeleted
            },
            onFailure = { error ->
                throw translateDataError(error, "delete", "vehicle")
            }
        )


    private suspend fun fetchVehiclesFromSource(): List<Vehicle> = coroutineScope {
        val warehousesById = async { warehouseRepository.getAll().associateBy { it.id } }
        val remoteResult = runCatching {
            remoteDataSource.getRawVehicles()
        }

        remoteResult
            .map { raw -> raw.mapNotNull { it.toDomain(warehousesById.await()) } }
            .getOrElse { error ->
                if (error is NetworkUnavailableException) {
                    localDataSource
                        .getAllVehicles()
                        .mapNotNull { it.toDomain(warehousesById.await()) }
                } else {
                    throw translateDataError(error, "fetch", "vehicles")
                }
            }
    }

}
