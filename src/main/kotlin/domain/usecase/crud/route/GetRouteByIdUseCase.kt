package domain.usecase.crud.route

import data.exception.translateDataError
import domain.model.exception.ResourceNotFoundException
import domain.model.Route
import domain.repository.RouteRepository

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Result<Route> {
        return runCatching { routeRepository.getById(id) }.fold(
            onSuccess = { route ->
                if (route != null) {
                    Result.success(route)
                } else {
                    Result.failure(ResourceNotFoundException())
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "fetch", "route"))
            }
        )
    }
}
