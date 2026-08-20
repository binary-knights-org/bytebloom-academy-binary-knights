package data.repository

import data.reader.CsvFileReader
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields
import domain.repository.PackageRepository

private const val EXPECTED_PACKAGE_FIELDS = 5
private const val CSV_DELIMITER = ","
private const val WEIGHT_UNIT_KG = "kg"
private const val INVALID_WEIGHT_DEFAULT = -1.0

private const val INDEX_ID = 0
private const val INDEX_WEIGHT = 1
private const val INDEX_ORIGIN_HUB = 2
private const val INDEX_DESTINATION_HUB = 3
private const val INDEX_PRIORITY = 4

private const val PRIORITY_URGENT = "URGENT"
private const val PRIORITY_STANDARD = "STANDARD"
private const val PRIORITY_LOW = "LOW"
private const val DEFAULT_PRIORITY = PRIORITY_LOW

class CsvPackageRepository(
    private val filePath: String,
    private val reader: CsvFileReader = CsvFileReader()
) : PackageRepository {

    override fun getAllPackages(): List<PackageRepository.PackageRecord> {
        val lines = reader.readLines(filePath)
        return extractPackages(lines)
    }

    private fun extractPackages(lines: List<String>): List<PackageRepository.PackageRecord> {
        return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
    }

    private fun parseLine(line: String): PackageRepository.PackageRecord? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_PACKAGE_FIELDS)) return null
        return mapFieldsToPackage(fields)
    }

    private fun mapFieldsToPackage(fields: List<String>): PackageRepository.PackageRecord {
        val packageId = fields[INDEX_ID]
        val weight = parseWeight(fields[INDEX_WEIGHT])
        val originHubId = fields[INDEX_ORIGIN_HUB]
        val destinationHubId = fields[INDEX_DESTINATION_HUB]
        val priority = parsePriority(fields[INDEX_PRIORITY])

        return PackageRepository.PackageRecord(
            packageId,
            weight,
            originHubId,
            destinationHubId,
            priority
        )
    }

    private fun parseWeight(weight: String): Double {
        val cleanWeight = weight.replace(WEIGHT_UNIT_KG, "", ignoreCase = true).trim()
        return cleanWeight.toDoubleOrNull() ?: INVALID_WEIGHT_DEFAULT
    }

    private fun parsePriority(priorityRaw: String): String {
        return when (val upperPriority = priorityRaw.uppercase()) {
            PRIORITY_URGENT, PRIORITY_STANDARD, PRIORITY_LOW -> upperPriority
            else -> DEFAULT_PRIORITY
        }
    }
}
