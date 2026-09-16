package domain.usecase.routing

import domain.algorithm.pathfinding.ShortestPathRouter
import domain.model.Warehouse

class FindOptimalPathUseCase(
    private val router: ShortestPathRouter
) {
     suspend operator fun invoke(
        origin: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {
        return router.findShortestPath(origin, destination)
    }
}
