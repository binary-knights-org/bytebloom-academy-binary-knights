package domain.usecase

import domain.model.Route
import domain.repository.RouteRepository

class GetAllRoutesUseCase(
    private val routeRepository: RouteRepository
) {
   operator fun invoke(): List<Route> {
   return routeRepository.getAllRoutes()
}

}
