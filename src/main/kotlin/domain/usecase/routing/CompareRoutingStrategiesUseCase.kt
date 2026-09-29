package domain.usecase.routing

import domain.model.Warehouse
import domain.model.RoutingComparison

class CompareRoutingStrategiesUseCase(
    private val findFewestHopsRouteUseCase: FindFewestHopsRouteUseCase,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
    private val findBidirectionalRouteUseCase: FindBidirectionalRouteUseCase
) {
  suspend  operator fun invoke(origin: Warehouse, destination: Warehouse): RoutingComparison {
        return RoutingComparison(
            fewestHops = findFewestHopsRouteUseCase(origin, destination),
            optimalDistance = findOptimalPathUseCase(origin, destination),
            bidirectional = findBidirectionalRouteUseCase(origin, destination)
        )
    }
}



