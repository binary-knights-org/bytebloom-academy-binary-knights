package parser

import dataholder.WarehouseRaw
import utils.hasValidFieldCount
import utils.parseCsvFields
import java.io.File
import utils.isMissingFile

private const val EXPECTED_WAREHOUSE_FIELDS = 3
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2


fun loadWarehouseData(filePath: String): List<WarehouseRaw> {
    val warehouseFile = File(filePath)
    if (isMissingFile(warehouseFile, parserName = "WarehouseParser")) {
        return emptyList()
    }
    val lines = warehouseFile.readLines().drop(HEADER_LINES_TO_SKIP)
    return processWarehouseLines(lines)
}

private fun processWarehouseLines(lines: List<String>): List<WarehouseRaw> {
    val validWarehouses = mutableListOf<WarehouseRaw>()

    for (line in lines) {
        if (line.isBlank()) continue
        val warehouse = parserWarehouseLine(line)
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

private fun parserWarehouseLine(line: String): WarehouseRaw? {
    val fields = parseCsvFields(line, CSV_DELIMITER)
    if (!hasValidFieldCount(fields,EXPECTED_WAREHOUSE_FIELDS, "WarehouseParser", line)) return null
    return mapFieldsToWarehouses(fields)
}
