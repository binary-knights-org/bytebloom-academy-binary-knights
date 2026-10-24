
package domain.algorithm.optimization

import domain.model.Warehouse
import domain.model.exception.InvalidDeliveryTourException

internal class TwoOptRouteOptimizer(
    private val distanceCalculator: GeographicDistanceCalculator =
        GeographicDistanceCalculator()
) {

    fun optimize(tour: List<Warehouse>): List<Warehouse> {
        validateTour(tour)
        return improveUntilNoBetterSwap(tour.toList())
    }

    private tailrec fun improveUntilNoBetterSwap(
        tour: List<Warehouse>
    ): List<Warehouse> {
        val bestSwap = findBestSwap(tour)
            ?: return tour

        return improveUntilNoBetterSwap(
            applySwap(tour, bestSwap)
        )
    }

    private fun findBestSwap(tour: List<Warehouse>): Swap? =
        candidateSwaps(tour)
            .map { swap ->
                swap to calculateDistanceReduction(
                    tour,
                    swap
                )
            }
            .filter { (_, reduction) ->
                reduction > MINIMUM_DISTANCE_REDUCTION_KM
            }
            .maxByOrNull { (_, reduction) -> reduction }
            ?.first

    private fun candidateSwaps(
        tour: List<Warehouse>
    ): List<Swap> =
        (FIRST_DELIVERY_INDEX until tour.lastIndex - 1)
            .flatMap { startIndex ->
                (startIndex + MINIMUM_REVERSAL_LENGTH - 1 until tour.lastIndex)
                    .map { endIndex ->
                        Swap(startIndex, endIndex)
                    }
            }

    private fun calculateDistanceReduction(
        tour: List<Warehouse>,
        swap: Swap
    ): Double {
        val previousWarehouse = tour[swap.startIndex - 1]
        val firstReversedWarehouse = tour[swap.startIndex]
        val lastReversedWarehouse = tour[swap.endIndex]
        val nextWarehouse = tour[swap.endIndex + 1]

        val currentDistance = distanceBetween(
            previousWarehouse,
            firstReversedWarehouse
        ) + distanceBetween(
            lastReversedWarehouse,
            nextWarehouse
        )

        val swappedDistance = distanceBetween(
            previousWarehouse,
            lastReversedWarehouse
        ) + distanceBetween(
            firstReversedWarehouse,
            nextWarehouse
        )

        return currentDistance - swappedDistance
    }

    private fun applySwap(
        tour: List<Warehouse>,
        swap: Swap
    ): List<Warehouse> =
        tour.take(swap.startIndex) +
                tour.subList(
                    swap.startIndex,
                    swap.endIndex + INCLUSIVE_END_OFFSET
                ).reversed() +
                tour.drop(swap.endIndex + INCLUSIVE_END_OFFSET)

    private fun distanceBetween(
        origin: Warehouse,
        destination: Warehouse
    ): Double =
        distanceCalculator.distanceBetween(origin, destination)

    private fun validateTour(tour: List<Warehouse>) {
        val hasMinimumStops = tour.size >= MINIMUM_TOUR_SIZE
        val returnsToOrigin =
            tour.firstOrNull()?.id == tour.lastOrNull()?.id

        if (!hasMinimumStops || !returnsToOrigin) {
            throw InvalidDeliveryTourException()
        }
    }

    private data class Swap(
        val startIndex: Int,
        val endIndex: Int
    )

    private companion object {
        const val MINIMUM_TOUR_SIZE = 3
        const val FIRST_DELIVERY_INDEX = 1
        const val MINIMUM_REVERSAL_LENGTH = 2
        const val INCLUSIVE_END_OFFSET = 1
        const val MINIMUM_DISTANCE_REDUCTION_KM = 0.0
    }
}
