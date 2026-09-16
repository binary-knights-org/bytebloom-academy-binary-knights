package domain.usecase.crud.route

import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.RouteValidator
import domain.validator.ValidationResult

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Route? {
        val validationResult = RouteValidator.validateId(id)
        if (validationResult is ValidationResult.Failure) {
            return null
        }

        return routeRepository.getRouteById(id)
    }
}
