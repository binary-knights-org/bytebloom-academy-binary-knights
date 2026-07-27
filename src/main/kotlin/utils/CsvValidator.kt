package utils


fun parseCsvFields(line: String, delimiter: String = ","): List<String> {
    return line.split(delimiter).map { it.trim() }
}

fun hasValidFieldCount(
    fields: List<String>,
    expectedCount: Int,
    parserName: String,
    rawLine: String
): Boolean {
    if (fields.size != expectedCount) {
        println("WARNING ($parserName): Skipping malformed row (expected $expectedCount fields): $rawLine")
        return false
    }
    return true
}