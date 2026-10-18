package domain.usecase.crud.route

import domain.model.Route
import domain.model.exception.ResourceNotFoundException
import domain.repository.RouteRepository

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Result<Route> =
        runCatching {
            routeRepository.getById(id)
                ?: throw ResourceNotFoundException()
        }
}
