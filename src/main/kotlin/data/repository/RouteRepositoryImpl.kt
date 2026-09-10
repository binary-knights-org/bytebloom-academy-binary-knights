package data.repository

import data.datasource.RouteDataSource
import data.local.csv.CsvRouteDataSource
import data.mapper.toDomain
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {
    private val dataSource: RouteDataSource =
        CsvRouteDataSource(filePath)
    override fun getAllRoutes(): List<Route> {
        val warehousesById = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }
        return dataSource
            .getRawRoutes()
            .mapNotNull { it.toDomain(warehousesById) }
    }
}
