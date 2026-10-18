package ui.cli

import domain.repository.PackageRepository
import domain.usecase.analytics.ClassifyPackageSectorUseCase

internal class PackageClassificationCli(
    private val packageRepository: PackageRepository,
    private val classifyPackageSectorUseCase: ClassifyPackageSectorUseCase
) {
    suspend fun run() {
        println("[PACKAGE CLASSIFICATION - KNN]")

        val latitude = readCoordinate("Enter destination latitude: ")
        val longitude = latitude?.let {
            readCoordinate("Enter destination longitude: ")
        }
        val weight = longitude?.let {
            readCoordinate("Enter package weight (kg): ")
        }

        if (latitude != null && longitude != null && weight != null) {
            classifyPackage(latitude, longitude, weight)
        }
    }

    private suspend fun classifyPackage(
        latitude: Double,
        longitude: Double,
        weight: Double
    ) {
        val result = runCatching {
            withLoadingIndicator("Classifying package") {
                val historicalPackages = packageRepository.getAll()

                classifyPackageSectorUseCase(
                    latitude = latitude,
                    longitude = longitude,
                    weight = weight,
                    historicalPackages = historicalPackages
                )
            }
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
