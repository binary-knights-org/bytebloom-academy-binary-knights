package data.local.csv

import data.dataholder.WarehouseRaw
import data.datasource.WarehouseDataSource
import data.processing.parser.WarehouseCsvParser
import data.processing.reader.CsvFileReader

class CsvWarehouseDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: WarehouseCsvParser = WarehouseCsvParser()
) : WarehouseDataSource {
    override fun getRawWarehouses(): List<WarehouseRaw> {
        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
    }
}
