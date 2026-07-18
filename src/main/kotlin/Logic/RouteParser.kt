package routes
import models.Route
import java.io.File

fun parseRoutes(filePath: String): List<Route> {
    val validRoutes = mutableListOf<Route>()
    val csvFile = File(filePath)

    if (!csvFile.exists()) {
        println("Warning: The track file is missing at path: $filePath")
        return validRoutes
    }

    val fileLines = csvFile.readLines()
    if (fileLines.isEmpty()) return validRoutes

    val headerRowIndex = 1
    val expectedFieldCount = 5

    for (i in headerRowIndex until fileLines.size) {
        val rawLine = fileLines[i].trim()
        if (rawLine.isEmpty()) continue

        val routeDataFields = rawLine.split(",")
        if (routeDataFields.size != expectedFieldCount) {
            println("A broken line was skipped: $rawLine")
            continue
        }

        try {
            val parsedRouteId = routeDataFields[0].trim()
            val parsedOriginHub = routeDataFields[1].trim()
            val parsedDestinationHub = routeDataFields[2].trim()

            val distanceText = routeDataFields[3].replace("km", "", ignoreCase = true).trim().toDouble()
            val delayText = routeDataFields[4].trim().toInt()

            val route = Route(
                routeId = parsedRouteId,
                originHubId = parsedOriginHub,
                destinationHubId = parsedDestinationHub,
                distanceKm = distanceText,
                typicalDelayMin = delayText
            )
            validRoutes.add(route)
        } catch (e: Exception) {
            println("Line conversion error: ${e.message} -> in line: $rawLine")
        }
    }
    return validRoutes
}