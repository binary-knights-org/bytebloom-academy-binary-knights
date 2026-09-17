package data.repository

import data.datasource.VehicleDataSource
import data.mapper.vehicles.toDomain
import data.mapper.vehicles.toRaw
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val dataSource: VehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    private var vehicles: List<Vehicle>? = null

    override suspend fun getAll(): List<Vehicle> =
        vehicles ?: fetchVehiclesFromSource().also { vehicles = it }

    override suspend fun getById(id: String): Vehicle? =
        getAll().find { it.id == id }

    override suspend fun create(item: Vehicle): Boolean =
        dataSource.createRawVehicle(item.toRaw()).also { isSuccess ->
            if (isSuccess) vehicles = null
        }

    override suspend fun update(item: Vehicle): Boolean =
        dataSource.updateRawVehicle(item.id, item.toRaw()).also { isSuccess ->
            if (isSuccess) vehicles = null
        }

    override suspend fun delete(id: String): Boolean =
        dataSource.deleteRawVehicle(id).also { isSuccess ->
            if (isSuccess) vehicles = null
        }

    private suspend fun fetchVehiclesFromSource(): List<Vehicle> =
        warehouseRepository.getAll()
            .associateBy { it.id }
            .let { warehousesById ->
                dataSource.getRawVehicles().mapNotNull { it.toDomain(warehousesById) }
            }
}
