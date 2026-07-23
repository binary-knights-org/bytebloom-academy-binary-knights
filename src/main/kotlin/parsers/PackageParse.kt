package parsers

import dataholder.PackageRaw
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
        println("WARNING (RouteParser): File not found at path: ${file.path}")
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
    val upperPriority = priorityRaw.uppercase()
    return when (upperPriority) {
        PRIORITY_URGENT, PRIORITY_STANDARD, PRIORITY_LOW -> upperPriority
        else -> DEFAULT_PRIORITY
    }
}

private fun parsePackageLine(line: String): PackageRaw? {
    if (line.isBlank()) return null
    val fields = line.split(CSV_DELIMITER).map { it.trim() }

    if (fields.size != EXPECTED_PACKAGE_FIELDS) {
        println("WARNING (PackageParser): Skipping malformed row (expected $EXPECTED_PACKAGE_FIELDS fields): $line")
        return null
    }

    val id = fields[INDEX_ID]
    val weight = parseWeight(fields[INDEX_WEIGHT])
    val destinationHubId = fields[INDEX_DESTINATION_HUB]
    val priority = parsePriority(fields[INDEX_PRIORITY])

    return PackageRaw(id, weight, destinationHubId, priority)
}
