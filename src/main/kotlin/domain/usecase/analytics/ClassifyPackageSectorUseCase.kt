package domain.usecase.analytics

import domain.algorithm.knn.KNearestNeighbors
import domain.model.Package
import domain.model.RegionalZone

class ClassifyPackageSectorUseCase(
    private val knn: KNearestNeighbors = KNearestNeighbors()
) {

    operator fun invoke(
        latitude: Double,
        longitude: Double,
        weight: Double,
        historicalPackages: List<Package>
    ): Result<RegionalZone> {
        return runCatching {
            knn(
                latitude = latitude,
                longitude = longitude,
                weight = weight,
                historicalPackages = historicalPackages
            )
        }
    }
}
