
package data.local.csv

import data.local.dataholder.PackageRaw
import data.local.datasource.CsvPackageDataSource

class CsvPackageDataSourceImpl(
    csvHandler: CsvFileHandler,
) : BaseCsvDataSource<PackageRaw>(csvHandler), CsvPackageDataSource {

    override suspend fun getAllPackages(): List<PackageRaw> = readAll()

    override fun parseLine(line: String): PackageRaw? =
        splitFields(line)
            .takeIf { it.size == EXPECTED_PACKAGE_FIELDS }
            ?.let(::mapFieldsToPackage)

    private fun mapFieldsToPackage(
        fields: List<String>
    ): PackageRaw? {
        val packageId = fields[INDEX_ID].uppercase()
        val originHub = fields[INDEX_ORIGIN_HUB].uppercase()
        val destinationHub = fields[INDEX_DESTINATION_HUB].uppercase()
        val priority = fields[INDEX_PRIORITY]
        val weight = parseWeight(fields[INDEX_WEIGHT])

        val hasEmptyText = listOf(
            packageId,
            originHub,
            destinationHub,
            priority
        ).any(String::isBlank)

        return fields
            .takeIf { !hasEmptyText && weight > MIN_WEIGHT }
            ?.let {
                PackageRaw(
                    packageId = packageId,
                    weight = weight,
                    originHubId = normalizePrefix(originHub, WAREHOUSE_PREFIX),
                    destinationHubId = normalizePrefix(
                        destinationHub,
                        WAREHOUSE_PREFIX
                    ),
                    priority = parsePriority(priority)
                )
            }
        }

    private fun parseWeight(weight: String): Double =
        weight.replace(WEIGHT_UNIT_KG, "", ignoreCase = true)
            .trim()
            .toDoubleOrNull()
            ?: INVALID_WEIGHT_DEFAULT

    private fun parsePriority(priorityRaw: String): String =
        priorityRaw.uppercase().let { priority ->
            when (priority) {
                PRIORITY_URGENT, PRIORITY_STANDARD, PRIORITY_LOW -> priority
                else -> DEFAULT_PRIORITY
            }
        }

    private companion object {
        const val EXPECTED_PACKAGE_FIELDS = 5
        const val WEIGHT_UNIT_KG = "kg"
        const val INVALID_WEIGHT_DEFAULT = -1.0

        const val INDEX_ID = 0
        const val INDEX_WEIGHT = 1
        const val INDEX_ORIGIN_HUB = 2
        const val INDEX_DESTINATION_HUB = 3
        const val INDEX_PRIORITY = 4
        const val MIN_WEIGHT = 0.0

        const val WAREHOUSE_PREFIX = "WH-"

        const val PRIORITY_URGENT = "URGENT"
        const val PRIORITY_STANDARD = "STANDARD"
        const val PRIORITY_LOW = "LOW"
        const val DEFAULT_PRIORITY = PRIORITY_LOW
    }
}
