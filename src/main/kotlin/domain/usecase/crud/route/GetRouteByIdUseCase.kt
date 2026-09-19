package domain.usecase.crud.route

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.routes.RouteIdValidator

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Result<Route> {
        val validation = idValidator.validate(id)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

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
