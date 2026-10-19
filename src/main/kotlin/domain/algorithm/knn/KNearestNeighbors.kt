
package domain.algorithm.knn

import domain.model.Package
import domain.model.RegionalZone
import domain.model.exception.InsufficientHistoricalPackagesException
import domain.model.exception.InvalidHistoricalPackageWeightException
import domain.model.exception.InvalidKnnWeightException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException

class KNearestNeighbors {

    operator fun invoke(
        latitude: Double,
        longitude: Double,
        weight: Double,
        historicalPackages: List<Package>
    ): RegionalZone {
        validateLatitude(latitude)
        validateLongitude(longitude)
        validateWeight(weight)
        validateHistoricalPackages(historicalPackages)

        val minWeight = historicalPackages.minOf { it.weight }
        val maxWeight = historicalPackages.maxOf { it.weight }

        val features = buildTrainingFeatures(
            historicalPackages = historicalPackages,
            minWeight = minWeight,
            maxWeight = maxWeight
        )

        val query = Feature(
            latitude = normalizeLatitude(latitude),
            longitude = normalizeLongitude(longitude),
            weight = normalizeWeight(weight, minWeight, maxWeight)
        )

        val nearestNeighbors = features
            .map { feature ->
                Neighbor(
                    sector = feature.sector,
                    distance = calculateDistance(query, feature)
                )
            }
            .sortedBy { it.distance }
            .take(K)

        return selectMajoritySector(nearestNeighbors)
    }

    private fun validateLatitude(latitude: Double) {
        if (
            !latitude.isFinite() ||
            latitude !in MIN_LATITUDE..MAX_LATITUDE
        ) {
            throw InvalidLatitudeException()
        }
    }

    private fun validateLongitude(longitude: Double) {
        if (
            !longitude.isFinite() ||
            longitude !in MIN_LONGITUDE..MAX_LONGITUDE
        ) {
            throw InvalidLongitudeException()
        }
    }

    private fun validateWeight(weight: Double) {
        if (!weight.isFinite() || weight <= MIN_WEIGHT) {
            throw InvalidKnnWeightException()
        }
    }

    private fun validateHistoricalPackages(
        historicalPackages: List<Package>
    ) {
        if (historicalPackages.size < K) {
            throw InsufficientHistoricalPackagesException()
        }

        if (
            historicalPackages.any {
                !it.weight.isFinite() || it.weight <= MIN_WEIGHT
            }
        ) {
            throw InvalidHistoricalPackageWeightException()
        }

        historicalPackages.forEach { historicalPackage ->
            validateLatitude(historicalPackage.destinationHub.latitude)
            validateLongitude(historicalPackage.destinationHub.longitude)
        }
    }

    private fun buildTrainingFeatures(
        historicalPackages: List<Package>,
        minWeight: Double,
        maxWeight: Double
    ): List<Feature> {
        return historicalPackages.map { historicalPackage ->
            Feature(
                latitude = normalizeLatitude(
                    historicalPackage.destinationHub.latitude
                ),
                longitude = normalizeLongitude(
                    historicalPackage.destinationHub.longitude
                ),
                weight = normalizeWeight(
                    historicalPackage.weight,
                    minWeight,
                    maxWeight
                ),
                sector = historicalPackage.destinationHub.regionalZone
            )
        }
    }

    private fun normalizeLatitude(latitude: Double): Double {
        return (latitude - MIN_LATITUDE) / LATITUDE_RANGE
    }

    private fun normalizeLongitude(longitude: Double): Double {
        return (longitude - MIN_LONGITUDE) / LONGITUDE_RANGE
    }

    private fun normalizeWeight(
        weight: Double,
        minWeight: Double,
        maxWeight: Double
    ): Double {
        if (maxWeight == minWeight) {
            return if (weight > maxWeight) NORMALIZED_MAX else NORMALIZED_MIN
        }

        return (weight - minWeight) / (maxWeight - minWeight)
    }

    private fun calculateDistance(
        first: Feature,
        second: Feature
    ): Double {
        val latitudeDifference = first.latitude - second.latitude
        val longitudeDifference = first.longitude - second.longitude
        val weightDifference = first.weight - second.weight

        return kotlin.math.sqrt(
            latitudeDifference * latitudeDifference +
                    longitudeDifference * longitudeDifference +
                    weightDifference * weightDifference
        )
    }

    private fun selectMajoritySector(
        neighbors: List<Neighbor>
    ): RegionalZone {
        val votes = neighbors.groupingBy { it.sector }.eachCount()
        val highestVoteCount = votes.values.maxOrNull()
            ?: throw InsufficientHistoricalPackagesException()

        val tiedSectors = votes
            .filterValues { it == highestVoteCount }
            .keys

        return neighbors.first { it.sector in tiedSectors }.sector
    }

    private data class Feature(
        val latitude: Double,
        val longitude: Double,
        val weight: Double,
        val sector: RegionalZone = RegionalZone.NORTH
    )

    private data class Neighbor(
        val sector: RegionalZone,
        val distance: Double
    )

    private companion object {
        const val K = 5

        const val MIN_WEIGHT = 0.0

        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0

        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0

        const val LATITUDE_RANGE = MAX_LATITUDE - MIN_LATITUDE
        const val LONGITUDE_RANGE = MAX_LONGITUDE - MIN_LONGITUDE

        const val NORMALIZED_MIN = 0.0
        const val NORMALIZED_MAX = 1.0
    }
}
