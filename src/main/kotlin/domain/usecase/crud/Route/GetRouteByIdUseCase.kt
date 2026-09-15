package domain.usecase.crud.Route

import domain.model.Route
import domain.repository.RouteRepository

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Route? {
        return routeRepository.getRouteById(id)
    }
}
