package ui.cli

import domain.repository.WarehouseRepository
import domain.usecase.routing.FindOptimalPathUseCase

internal suspend fun runRouteCalculation(
    warehouseRepository: WarehouseRepository,
    findOptimalPathUseCase: FindOptimalPathUseCase
) {
    print("Enter origin warehouse ID: ")
    val originId = readlnOrNull()?.trim()

    if (originId.isNullOrBlank()) {
        println("Origin warehouse ID cannot be empty.")
        return
    }

    print("Enter destination warehouse ID: ")
    val destinationId = readlnOrNull()?.trim()

    if (destinationId.isNullOrBlank()) {
        println("Destination warehouse ID cannot be empty.")
        return
    }

    val path = withLoadingIndicator("Calculating route") {
        val origin = warehouseRepository.getById(originId)

        if (origin == null) {
            println("Origin warehouse not found.")
            null
        } else {
            val destination = warehouseRepository.getById(destinationId)

            if (destination == null) {
                println("Destination warehouse not found.")
                null
            } else {
                findOptimalPathUseCase(origin, destination)
            }
        }
    }

    if (path == null) {
        println("No route found, or one of the warehouses was not found.")
    } else {
        println("Route found:")
        println(path.joinToString(" -> ") { it.name })
    }
}

