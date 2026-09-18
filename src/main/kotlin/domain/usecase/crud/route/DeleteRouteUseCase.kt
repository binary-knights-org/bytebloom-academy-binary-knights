package domain.usecase.crud.route

import domain.exception.EntityValidationException
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.RouteIdValidator

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {
        val validation = idValidator.validate(id)

        if (validation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot delete route: invalid route ID."
            )
        }

        return routeRepository.delete(id)
    }
}