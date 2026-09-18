package domain.usecase.crud.route

import domain.exception.EntityValidationException
import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.RouteIdValidator

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Route? {
        val validation = idValidator.validate(id)

        if (validation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot get route: invalid route ID."
            )
        }

        return routeRepository.getById(id)
    }
}
