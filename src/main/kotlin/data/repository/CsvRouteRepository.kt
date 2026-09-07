package data.repository

import data.mapper.toDomain
import data.processing.parser.RouteCsvParser
import data.processing.reader.CsvFileReader
import domain.model.Route
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

class CsvRouteRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: RouteCsvParser = RouteCsvParser()
) : RouteRepository {

    override fun getAllRoutes(): List<Route> {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }

        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> parser.parseLine(line)?.toDomain(warehousesById) }
    }
}
