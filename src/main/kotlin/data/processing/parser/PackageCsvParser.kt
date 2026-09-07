package data.processing.parser

import data.dataholder.PackageRaw
import data.utils.hasValidFieldCount
import data.utils.parseCsvFields

class PackageCsvParser {
    fun parseLine(line: String): PackageRaw? {
        val fields = parseCsvFields(line, CSV_DELIMITER)
        if (!hasValidFieldCount(fields, EXPECTED_PACKAGE_FIELDS)) {
            return null
        }
        return mapFieldsToPackage(fields)
    }

    private fun mapFieldsToPackage(fields: List<String>): PackageRaw {
        val packageId = fields[INDEX_ID]
        val weight = parseWeight(fields[INDEX_WEIGHT])
        val originHubId = fields[INDEX_ORIGIN_HUB]
        val destinationHubId = fields[INDEX_DESTINATION_HUB]
        val priority = parsePriority(fields[INDEX_PRIORITY])

        return PackageRaw(
            packageId = packageId,
            weight = weight,
            originHubId = originHubId,
            destinationHubId = destinationHubId,
            priority = priority
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

    private companion object {
        const val EXPECTED_PACKAGE_FIELDS = 5
        const val CSV_DELIMITER = ","
        const val WEIGHT_UNIT_KG = "kg"
        const val INVALID_WEIGHT_DEFAULT = -1.0

        const val INDEX_ID = 0
        const val INDEX_WEIGHT = 1
        const val INDEX_ORIGIN_HUB = 2
        const val INDEX_DESTINATION_HUB = 3
        const val INDEX_PRIORITY = 4

        const val PRIORITY_URGENT = "URGENT"
        const val PRIORITY_STANDARD = "STANDARD"
        const val PRIORITY_LOW = "LOW"
        const val DEFAULT_PRIORITY = PRIORITY_LOW
    }
}
