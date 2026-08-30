package domain.usecase

import domain.model.Warehouse


class CompareRoutingStrategiesUseCase(
    private val findFewestHopsRouteUseCase: FindFewestHopsRouteUseCase,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
    private val findBidirectionalRouteUseCase: FindBidirectionalRouteUseCase
) {
    operator fun invoke(origin: Warehouse, destination: Warehouse): RoutingComparison {
        return RoutingComparison(
            fewestHops = findFewestHopsRouteUseCase(origin, destination),
            optimalDistance = findOptimalPathUseCase(origin, destination),
            bidirectional = findBidirectionalRouteUseCase(origin, destination)
        )
    }
}

data class RoutingComparison(
    val fewestHops: List<Warehouse>?,
    val optimalDistance: List<Warehouse>?,
    val bidirectional: List<Warehouse>?
)

