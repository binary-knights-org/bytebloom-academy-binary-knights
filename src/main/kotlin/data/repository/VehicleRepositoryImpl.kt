package data.repository

import data.local.datasource.CsvVehicleDataSource
import data.mapper.vehicles.toDomain
import data.mapper.vehicles.toRaw
import data.remote.datasource.RemoteVehicleDataSource
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import java.util.logging.Logger

class VehicleRepositoryImpl(
    private val remoteDataSource: RemoteVehicleDataSource,
    private val localDataSource: CsvVehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : BaseRepositoryImpl<Vehicle>("vehicle"), VehicleRepository {

    private val logger = Logger.getLogger(VehicleRepositoryImpl::class.java.simpleName)

    override suspend fun getAll(): List<Vehicle> =
        cache ?: fetchVehiclesFromSource().also { cache = it }

    override suspend fun getById(id: String): Vehicle? =
        getAll().find { it.id == id }

    override suspend fun create(item: Vehicle): Vehicle {
        val rawResult = executeWriteOp("create") {
            remoteDataSource.createRawVehicle(item.toRaw())
        }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return rawResult.toDomain(warehousesById) ?: item
    }

    override suspend fun update(item: Vehicle): Vehicle {
        val rawResult = executeWriteOp("update") {
            remoteDataSource.updateRawVehicle(item.id, item.toRaw())
        }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return rawResult.toDomain(warehousesById) ?: item
    }

    override suspend fun delete(id: String) {
        executeWriteOp("delete") {
            remoteDataSource.deleteRawVehicle(id)
        }
    }

    private suspend fun fetchVehiclesFromSource(): List<Vehicle> =
        fetchWithFallback(
            warehouseRepository = warehouseRepository,
            remoteFetch = { remoteDataSource.getRawVehicles() },
            localFetch = { localDataSource.getAllVehicles() },
            mapToDomain = { raw, map -> raw.toDomain(map) },
            logger = logger
        )
}
