package parsers

import dataholder.WarehouseRaw
import java.io.File

private const val EXPECTED_WAREHOUSE_FIELDS = 3
private const val CSV_DELIMITER = ","
private const val HEADER_LINES_TO_SKIP = 1

private const val INDEX_ID = 0
private const val INDEX_NAME = 1
private const val INDEX_REGIONAL_ZONE = 2






fun loadWarehouseData(filePath: String): List<WarehouseRaw> {
    val warehouseFile = File(filePath)
    if (isMissingFile(warehouseFile)) {
        println("WARNING (WarehouseParser): File not found at path: $filePath")
        return emptyList()
    }
    val lines = warehouseFile.readLines().drop(HEADER_LINES_TO_SKIP)
    return processWarehouseLines(lines)
}


private fun isMissingFile(file: File): Boolean {
        if (!file.exists()) {
            println("WARNING (RouteParser): File not found at path: ${file.path}")
            return true
        }
        return false
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





private fun parserWarehouseLine(line: String): WarehouseRaw? {
    if (line.isBlank()) return null
    val fields = line.split(CSV_DELIMITER).map { it.trim() }

    if (fields.size != EXPECTED_WAREHOUSE_FIELDS) {
        println("WARNING (WarehouseParser): Skipping malformed row (expected $EXPECTED_WAREHOUSE_FIELDS fields): $line")
        return null
    }

    val id = fields[INDEX_ID]
    val name = fields[INDEX_NAME]
    val regionalZone = fields[INDEX_REGIONAL_ZONE]

    return WarehouseRaw(id, name, regionalZone)
}
