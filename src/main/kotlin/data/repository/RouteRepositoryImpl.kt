package data.repository

import data.datasource.RouteDataSource
import data.mapper.toDomain
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val dataSource: RouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    override fun getAllRoutes(): List<Route> {
        val warehousesById = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return dataSource
            .getRawRoutes()
            .mapNotNull { it.toDomain(warehousesById) }
    }
}
