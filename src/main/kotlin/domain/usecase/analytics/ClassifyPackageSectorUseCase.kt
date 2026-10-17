
package domain.usecase.analytics

import domain.model.Package
import domain.model.RegionalZone
import kotlin.math.sqrt

class ClassifyPackageSectorUseCase {

    operator fun invoke(
        latitude: Double,
        longitude: Double,
        weight: Double,
        historicalPackages: List<Package>
    ): Result<RegionalZone> = runCatching {
        validateInputs(latitude, longitude, weight, historicalPackages)
        classify(latitude, longitude, weight, historicalPackages)
    }

    private fun validateInputs(
        latitude: Double,
        longitude: Double,
        weight: Double,
        historicalPackages: List<Package>
    ) {
        require(
            latitude.isFinite() &&
                    latitude in MIN_LATITUDE..MAX_LATITUDE
        ) {
            "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."
        }

        require(
            longitude.isFinite() &&
                    longitude in MIN_LONGITUDE..MAX_LONGITUDE
        ) {
            "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
        }

        require(weight.isFinite() && weight > MIN_WEIGHT) {
            "Package weight must be finite and positive."
        }

        require(historicalPackages.size >= K) {
            "At least $K historical packages are required."
        }

        require(historicalPackages.all {
            it.weight.isFinite() && it.weight > MIN_WEIGHT
        }) {
            "Historical package weights must be finite and positive."
        }
    }

    private fun classify(
        latitude: Double,
        longitude: Double,
        weight: Double,
        historicalPackages: List<Package>
    ): RegionalZone {
        val minWeight = historicalPackages.minOf { it.weight }
        val maxWeight = historicalPackages.maxOf { it.weight }

        val query = Features(
            latitude = normalizeLatitude(latitude),
            longitude = normalizeLongitude(longitude),
            weight = normalizeWeight(weight, minWeight, maxWeight)
        )

        val nearestNeighbors = historicalPackages
            .map { historicalPackage ->
                val hub = historicalPackage.destinationHub

                val features = Features(
                    latitude = normalizeLatitude(hub.latitude),
                    longitude = normalizeLongitude(hub.longitude),
                    weight = normalizeWeight(
                        historicalPackage.weight,
                        minWeight,
                        maxWeight
                    )
                )

                Neighbor(
                    sector = hub.regionalZone,
                    distance = euclideanDistance(query, features)
                )
            }
            .sortedBy { it.distance }
            .take(K)

        return selectWinningSector(nearestNeighbors)
    }

    private fun normalizeWeight(
        value: Double,
        minWeight: Double,
        maxWeight: Double
    ): Double {
        if (maxWeight == minWeight) {
            return if (value > maxWeight) NORMALIZED_MAX else NORMALIZED_MIN
        }

        return ((value - minWeight) / (maxWeight - minWeight))
            .coerceIn(NORMALIZED_MIN, NORMALIZED_MAX)
    }

    private fun normalizeLatitude(value: Double): Double =
        (value - MIN_LATITUDE) / LATITUDE_RANGE

    private fun normalizeLongitude(value: Double): Double =
        (value - MIN_LONGITUDE) / LONGITUDE_RANGE

    private fun selectWinningSector(
        nearestNeighbors: List<Neighbor>
    ): RegionalZone {
        val votes = nearestNeighbors
            .groupingBy { it.sector }
            .eachCount()

        val highestVoteCount = votes.values.maxOrNull()
            ?: error("Unable to determine a sector.")

        val winningSectors = votes
            .filterValues { it == highestVoteCount }
            .keys

        return nearestNeighbors
            .first { it.sector in winningSectors }
            .sector
    }

    private fun euclideanDistance(
        first: Features,
        second: Features
    ): Double {
        val latitudeDiff = first.latitude - second.latitude
        val longitudeDiff = first.longitude - second.longitude
        val weightDiff = first.weight - second.weight

        return sqrt(
            latitudeDiff * latitudeDiff +
                    longitudeDiff * longitudeDiff +
                    weightDiff * weightDiff
        )
    }

    private data class Features(
        val latitude: Double,
        val longitude: Double,
        val weight: Double
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
