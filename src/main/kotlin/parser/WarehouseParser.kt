package parser

import dataholder.WarehouseRaw
import utils.hasValidFieldCount
import utils.parseCsvFields
import java.io.File
import utils.checkFileExists

private const val EXPECTED_WAREHOUSE_FIELDS = 3
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2


fun loadWarehouseData(filePath: String): List<WarehouseRaw> {
    val file = File(filePath)
    if (checkFileExists(file, parserName = "WarehouseParser")) {
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

private fun mapFieldsToWarehouses(fields: List<String>): WarehouseRaw {
    val hubId = fields[INDEX_ID]
    val hubName = fields[INDEX_NAME]
    val regionalZone = fields[INDEX_REGIONAL_ZONE]

    return WarehouseRaw(hubId, hubName, regionalZone)
}

private fun parserLine(line: String): WarehouseRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields,EXPECTED_WAREHOUSE_FIELDS, "WarehouseParser", line)) return null
    return mapFieldsToWarehouses(fields)
}
