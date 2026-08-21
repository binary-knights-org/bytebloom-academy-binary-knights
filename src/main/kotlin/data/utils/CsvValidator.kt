package data.utils


fun parseCsvFields(line: String, delimiter: String = ","): List<String> {
    return line.split(delimiter).map { it.trim() }
}

fun hasValidFieldCount(fields: List<String>, expectedCount: Int): Boolean {
    return fields.size == expectedCount
}
