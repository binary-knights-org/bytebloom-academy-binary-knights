package parser

import dataholder.PackageRaw
import utils.hasValidFieldCount
import utils.parseCsvFields
import java.io.File

private const val EXPECTED_PACKAGE_FIELDS = 4
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1
private const val WEIGHT_UNIT_KG = "kg"
private const val INVALID_WEIGHT_DEFAULT = -1.0

private const val INDEX_ID = 0
private const val INDEX_WEIGHT = 1
private const val INDEX_DESTINATION_HUB = 2
private const val INDEX_PRIORITY = 3

private const val PRIORITY_URGENT = "URGENT"
private const val PRIORITY_STANDARD = "STANDARD"
private const val PRIORITY_LOW = "LOW"
private const val DEFAULT_PRIORITY = PRIORITY_LOW

fun loadPackageData(filePath: String): List<PackageRaw> {
    val packageFile = File(filePath)
    if (isMissingFile(packageFile)) {
        return emptyList()
    }
    val lines = packageFile.readLines().drop(HEADER_LINES_TO_SKIP)
    return processPackageLines(lines)
}

private fun isMissingFile(file: File): Boolean {
    if (!file.exists()) {
        println("WARNING (PackageParser): File not found at path: ${file.path}")
        return true
    }
    return false
}




private fun processPackageLines(lines: List<String>): List<PackageRaw> {
    val validPackage = mutableListOf<PackageRaw>()

    for (line in lines) {
        if (line.isBlank()) continue
        val Package = parsePackageLine(line)
        if (Package != null) {
            validPackage.add(Package)
        }
    }

    return validPackage
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

private fun mapFieldsToPackages(fields: List<String>): PackageRaw {
    val packageId = fields[INDEX_ID]
    val weight = parseWeight(fields[INDEX_WEIGHT])
    val destinationHubId = fields[INDEX_DESTINATION_HUB]
    val priority = parsePriority(fields[INDEX_PRIORITY])

    return PackageRaw(packageId, weight, destinationHubId, priority)
}

private fun parsePackageLine(line: String): PackageRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields, EXPECTED_PACKAGE_FIELDS, "PackageParser", line)) return null
    return mapFieldsToPackages(fields)
}
