package data.local.csv

import data.dataholder.RouteRaw
import data.datasource.RouteDataSource
import data.processing.parser.RouteCsvParser
import data.processing.reader.CsvFileReader

class CsvRouteDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: RouteCsvParser = RouteCsvParser()
) : RouteDataSource {
    override fun getRawRoutes(): List<RouteRaw> {
        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
    }
}
