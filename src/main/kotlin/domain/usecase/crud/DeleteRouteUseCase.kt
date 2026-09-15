package domain.usecase.crud

import domain.repository.RouteRepository

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        return routeRepository.deleteRoute(id)
    }
}
