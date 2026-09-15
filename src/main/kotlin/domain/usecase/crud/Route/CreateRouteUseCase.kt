package domain.usecase.crud.Route

import domain.model.Route
import domain.repository.RouteRepository

class CreateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(route: Route): Boolean {
        return routeRepository.createRoute(route)
    }
}
