package domain.usecase.crud.route

import domain.model.Route
import domain.repository.RouteRepository
import domain.model.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.routes.RouteIdValidator

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Route> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val route = routeRepository.getById(id)
        return if (route != null) {
            ValidationResult.Success(route)
        } else {
            ValidationResult.Failure(listOf(EntityNotFoundException("Route", id)))
        }
    }
}
