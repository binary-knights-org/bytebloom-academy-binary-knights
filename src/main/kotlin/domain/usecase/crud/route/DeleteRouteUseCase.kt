package domain.usecase.crud.route

import domain.repository.RouteRepository
import domain.validator.RouteValidator
import domain.validator.ValidationResult

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        val validationResult = RouteValidator.validateId(id)
        if (validationResult is ValidationResult.Failure) {
            return false
        }

        return routeRepository.deleteRoute(id)
    }
}
