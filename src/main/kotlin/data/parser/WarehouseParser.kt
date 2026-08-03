package data.parser

import data.dataholder.WarehouseRaw
import java.io.File

private const val EXPECTED_WAREHOUSE_FIELDS = 5
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2
private const val INDEX_LATITUDE = 3
private const val INDEX_LONGITUDE = 4

fun loadWarehouseData(filePath: String): List<WarehouseRaw> {
    val file = File(filePath)
    if (!checkFileExists(file)) {
        return emptyList()
    }
    val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
    return extractWarehouse(lines)
}

private fun extractWarehouse(lines: List<String>): List<WarehouseRaw> {
    return lines.filter { it.isNotBlank() }.mapNotNull { parseLine(it) }
}

private fun mapFieldsToWarehouses(fields: List<String>, rawLine: String): WarehouseRaw? {
    val hubId = fields[INDEX_ID]
    val hubName = fields[INDEX_NAME]
    val regionalZone = fields[INDEX_REGIONAL_ZONE]
    val latitude = fields[INDEX_LATITUDE].toDoubleOrNull()
    val longitude = fields[INDEX_LONGITUDE].toDoubleOrNull()

    return when {
        (latitude == null || longitude == null) -> null
        else -> WarehouseRaw(hubId, hubName, regionalZone, latitude, longitude)
    }
}

private fun parseLine(line: String): WarehouseRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields, EXPECTED_WAREHOUSE_FIELDS)) return null
    return mapFieldsToWarehouses(fields, line)
}
