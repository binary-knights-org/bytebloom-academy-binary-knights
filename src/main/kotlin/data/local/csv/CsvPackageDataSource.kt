package data.local.csv

import data.dataholder.PackageRaw
import data.datasource.PackageDataSource
import data.processing.parser.PackageCsvParser
import data.processing.reader.CsvFileReader

class CsvPackageDataSource(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader(),
    private val parser: PackageCsvParser = PackageCsvParser()
) : PackageDataSource {
    override fun getRawPackages(): List<PackageRaw> {
        return reader.readLines(filePath)
            .filter { it.isNotBlank() }
            .mapNotNull { parser.parseLine(it) }
    }
}

