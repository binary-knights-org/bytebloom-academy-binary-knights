package domain.usecase.crud.route

import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.RouteValidator
import domain.validator.ValidationResult

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String): Route? {
        val isValid = RouteValidator.validateId(id) is ValidationResult.Success
        return if (isValid) routeRepository.getById(id) else null
    }
}
