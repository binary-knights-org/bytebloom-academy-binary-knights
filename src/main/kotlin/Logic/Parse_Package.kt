
package Logic
import models.Package

    fun parsePackage(line: String): Package? {

        val parts = mutableListOf<String>()
        var currentPart = StringBuilder()

        for (char in line) {
            if (char == ',') {

                parts.add(currentPart.toString().trim())
                currentPart = StringBuilder()
            } else {
                currentPart.append(char)
            }
        }

        parts.add(currentPart.toString().trim())


        if (parts.size != 4) {
            println("Diagnostic Warning: Skipping malformed row: $line")
            return null
        }


        val id = parts[0]
        val weightStr = parts[1]
        val destinationHubId = parts[2]
        val priorityRaw = parts[3].uppercase()


        val weight = try {

            val cleanWeight = weightStr.replace("kg", "", ignoreCase = true)
            cleanWeight.toDouble()
        } catch (e: NumberFormatException) {
            -1.0
        }


        val priority = if (priorityRaw == "URGENT" || priorityRaw == "STANDARD" || priorityRaw == "LOW") {
            priorityRaw
        } else {
            "LOW"
        }

        return Package(id, weight, destinationHubId, priority)
    }
