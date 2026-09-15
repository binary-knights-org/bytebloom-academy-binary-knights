package domain.usecase.crud.route

import domain.model.Route
import domain.repository.RouteRepository

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(route: Route): Boolean {
        return routeRepository.updateRoute(route)
    }
}
