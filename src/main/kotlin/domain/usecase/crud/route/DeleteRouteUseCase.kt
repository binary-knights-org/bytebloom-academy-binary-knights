package domain.usecase.crud.route

import domain.repository.RouteRepository

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> =
        runCatching {
            routeRepository.delete(id)
        }
}
