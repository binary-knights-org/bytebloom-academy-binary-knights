
package parsers
import dataholders.Package

    fun parsePackage(line: String): Package? {

        val fields = mutableListOf<String>()
        var currentPart = StringBuilder()

        for (char in line) {
            if (char == ',') {

                fields.add(currentPart.toString().trim())
                currentPart = StringBuilder()
            } else {
                currentPart.append(char)
            }
        }

        fields.add(currentPart.toString().trim())
        val EXPECTED_PACKAGE_FIELDS =4

        if (fields.size != EXPECTED_PACKAGE_FIELDS) {
            println("Diagnostic Warning: Skipping malformed row: $line")
            return null
        }


        val id = fields[0]
        val weightStr = fields[1]
        val destinationHubId = fields[2]
        val priorityRaw = fields[3].uppercase()


        val weight = try {

            val cleanWeight = weightStr.replace("kg", "", ignoreCase = true)
            cleanWeight.toDouble()
        } catch (e: NumberFormatException) {
            -1.0
        }


        val priority = when (priorityRaw) {
            "URGENT", "STANDARD", "LOW" -> priorityRaw
            else -> "LOW"
        }
        return Package(id, weight, destinationHubId, priority)
    }
