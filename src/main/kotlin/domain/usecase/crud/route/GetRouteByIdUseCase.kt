package domain.usecase.crud.route

import domain.exception.DatabaseConflictException
import domain.exception.ResourceNotFoundException
import domain.model.Route
import domain.repository.RouteRepository

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository,
) {
    suspend operator fun invoke(id: String): Result<Route> {

        return runCatching { routeRepository.getById(id) }.fold(
            onSuccess = { route ->
                if (route != null) {
                    Result.success(route)
                } else {
                    Result.failure(ResourceNotFoundException("Route with ID '$id' was not found."))
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to fetch route with ID '$id': ${error.message}", error
                    )
                )
            }
        )
    }
}
