package domain.usecase.crud.route

import domain.repository.RouteRepository
import domain.validator.RouteValidator
import domain.validator.ValidationResult

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        val isValid = RouteValidator.validateId(id) is ValidationResult.Invalid
        return isValid && routeRepository.delete(id)
    }
}
