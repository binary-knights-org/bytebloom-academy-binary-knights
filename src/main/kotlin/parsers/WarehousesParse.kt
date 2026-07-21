package parsers

import dataholders.Hub
import java.io.File

private const val EXPECTED_WAREHOUSE_FIELDS = 3
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2

private fun parserWarehouseLine(line: String): Hub? {
    if (line.isBlank()) return null
    val fields = line.split(CSV_DELIMITER).map { it.trim() }

    if (fields.size != EXPECTED_WAREHOUSE_FIELDS) {
        println("WARNING (WarehouseParser): Skipping malformed row (expected $EXPECTED_WAREHOUSE_FIELDS fields): $line")
        return null
    }

    val id = fields[INDEX_ID]
    val name = fields[INDEX_NAME]
    val regionalZone = fields[INDEX_REGIONAL_ZONE]

    return Hub(id, name, regionalZone)
}

fun loadWarehouseData(filePath: String): List<Hub> {
    val warehouseFile = File(filePath)
    if (!warehouseFile.exists()) {
        println("WARNING (WarehouseParser): File not found at path: $filePath")
        return emptyList()
    }

    val validWarehouses = mutableListOf<Hub>()

    try {
        val lines = warehouseFile.readLines().drop(HEADER_LINES_TO_SKIP)
        for (line in lines) {
            val warehouse = parserWarehouseLine(line)
            if (warehouse != null) validWarehouses.add(warehouse)
        }
    } catch (e: Exception) {
        println("ERROR (WarehouseParser): Failed to read CSV file: ${e.message}")
    }

    return validWarehouses
}