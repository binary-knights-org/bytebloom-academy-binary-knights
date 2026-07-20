package org.example.Logic

import dataholders.Warehouse
import java.io.File

fun parseWarehouses(filePath: String): List<Warehouse> {

    val validWarehouses = mutableListOf<Warehouse>()
    val csvFile = File(filePath)

    if (!csvFile.exists()) {
        println("Warning: The warehouse file is missing at path: $filePath")
        return validWarehouses
    }

    val fileLines = csvFile.readLines()
    if (fileLines.isEmpty()) return validWarehouses

    val HEADER_ROW_INDEX = 1
    val EXPECTED_FIELD_COUNT = 3

    for (i in HEADER_ROW_INDEX until fileLines.size) {

        val rawLine = fileLines[i].trim()

        if (rawLine.isEmpty()) continue

        val warehouseDataFields = rawLine.split(",")

        if (warehouseDataFields.size != EXPECTED_FIELD_COUNT) {
            println("A broken line was skipped: $rawLine")
            continue
        }

        val warehouse = Warehouse(
            id = warehouseDataFields[0].trim(),
            name = warehouseDataFields[1].trim(),
            regionalZone = warehouseDataFields[2].trim()
        )

        validWarehouses.add(warehouse)
    }

    return validWarehouses
}