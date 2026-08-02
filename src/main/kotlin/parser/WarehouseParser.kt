package parser

import dataholder.WarehouseRaw
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
    if (checkFileExists(file, "WarehouseParser")) {
        return emptyList()
    }
    val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
    return extractWarehouse(lines)
}

private fun extractWarehouse(lines: List<String>): List<WarehouseRaw> {
    val validWarehouses = mutableListOf<WarehouseRaw>()

    for (line in lines) {
        if (line.isBlank()) continue
        val warehouse = parserLine(line)
        if (warehouse != null) {
            validWarehouses.add(warehouse)
        }
    }

    return validWarehouses
}

private fun mapFieldsToWarehouses(fields: List<String>, rawLine: String): WarehouseRaw? {
    val hubId = fields[INDEX_ID]
    val hubName = fields[INDEX_NAME]
    val regionalZone = fields[INDEX_REGIONAL_ZONE]
    val latitude = fields[INDEX_LATITUDE].toDoubleOrNull() ?: return skipInvalidRow(rawLine)
    val longitude = fields[INDEX_LONGITUDE].toDoubleOrNull() ?: return skipInvalidRow(rawLine)

    return WarehouseRaw(hubId, hubName, regionalZone,latitude,longitude)
}

private fun skipInvalidRow(rawLine: String): WarehouseRaw? {
    println("WARNING (WarehouseParser): Skipping row (invalid numeric data): $rawLine")
    return null
}

private fun parserLine(line: String): WarehouseRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields,EXPECTED_WAREHOUSE_FIELDS, "WarehouseParser", line)) return null
    return mapFieldsToWarehouses(fields, line)
}
