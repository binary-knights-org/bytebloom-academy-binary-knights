package domain.usecase.routing

import domain.model.routing.RoutingComparison

import domain.model.Warehouse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CompareRoutingStrategiesUseCase(
    private val findFewestHopsRouteUseCase: FindFewestHopsRouteUseCase,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
    private val findBidirectionalRouteUseCase: FindBidirectionalRouteUseCase
) {
    suspend operator fun invoke(origin: Warehouse, destination: Warehouse): RoutingComparison =
        coroutineScope {
            val fewestHops = async { findFewestHopsRouteUseCase(origin, destination) }
            val optimalDistance = async { findOptimalPathUseCase(origin, destination) }
            val bidirectional = async { findBidirectionalRouteUseCase(origin, destination) }

            RoutingComparison(
                fewestHops.await(), optimalDistance.await(), bidirectional.await()
            )
        }
    }


