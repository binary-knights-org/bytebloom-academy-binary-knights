package data.repository

import data.mapper.toDomain
import data.processing.parser.PackageCsvParser
import data.processing.reader.CsvFileReader
import domain.model.Package
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class CsvPackageRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: PackageCsvParser = PackageCsvParser()
) : PackageRepository {

    override fun getAllPackages(): List<Package> {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }

        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { line -> parser.parseLine(line)?.toDomain(warehousesById) }
    }
}
