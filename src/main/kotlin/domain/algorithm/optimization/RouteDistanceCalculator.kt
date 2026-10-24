package domain.algorithm.optimization

import domain.model.Warehouse

internal class RouteDistanceCalculator(
    private val geographicDistanceCalculator: GeographicDistanceCalculator =
        GeographicDistanceCalculator()
) {

    fun calculateTotalDistanceKm(tour: List<Warehouse>): Double =
        tour.zipWithNext().sumOf { (origin, destination) ->
            geographicDistanceCalculator.distanceBetween(origin, destination)
        }
}
