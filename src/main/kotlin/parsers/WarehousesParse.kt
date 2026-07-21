package parsers

import dataholders.Hub
import java.io.File

private const val EXXPECTED_WAREHOUSE_FIELDS = 3
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2

private fun parserWarehouseLine(line: String): Hub? {
    if (line.isBlank()) return null
    val fields = line.split(CSV_DELIMITER).map { it.trim() }

    if (fields.size != EXXPECTED_WAREHOUSE_FIELDS) {
        println("A broken line was skipped: $line")
        return null
    }

    return Hub(
        id = fields[INDEX_ID],
        name = fields[INDEX_NAME],
        regionalZone = fields[INDEX_REGIONAL_ZONE]
    )
}

fun loadWarehouseData(filePath: String): List<Hub> {
    val warehouseFile = File(filePath)
    if (!warehouseFile.exists()) {
        println("Warning: The warehouse file is missing at path: $filePath")
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
        println("Diagnostic Error: Failed to read warehouse file due to: ${e.message}")
    }

    return validWarehouses
}