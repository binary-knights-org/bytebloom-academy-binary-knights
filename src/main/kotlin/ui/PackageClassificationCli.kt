package ui

import domain.repository.PackageRepository
import domain.usecase.analytics.ClassifyPackageSectorUseCase

internal class PackageClassificationCli(
    private val packageRepository: PackageRepository,
    private val classifyPackageSectorUseCase: ClassifyPackageSectorUseCase
) {
    suspend fun run() {
        println("[PACKAGE CLASSIFICATION - KNN]")

        val latitude = readCoordinate("Enter destination latitude: ")
            ?: return

        val longitude = readCoordinate("Enter destination longitude: ")
            ?: return

        val weight = readCoordinate("Enter package weight (kg): ")
            ?: return

        val result = withLoadingIndicator("Classifying package") {
            val historicalPackages = packageRepository.getAll()

            classifyPackageSectorUseCase(
                latitude = latitude,
                longitude = longitude,
                weight = weight,
                historicalPackages = historicalPackages
            )
        }

        result
            .onSuccess { sector ->
                println("Predicted regional zone: $sector")
            }
            .onFailure { exception ->
                println(
                    "Classification failed: " +
                            (exception.message ?: "Unknown error.")
                )
            }
    }

    private fun readCoordinate(prompt: String): Double? {
        print(prompt)

        val input = readlnOrNull()?.trim()
        val value = input?.toDoubleOrNull()

        if (value == null) {
            println("Invalid input. Please enter a valid number.")
        }

        return value
    }
}

