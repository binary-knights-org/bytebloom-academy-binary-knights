package domain.usecase.crud.route

import domain.exception.EntityValidationException
import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.CreateRouteValidator

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator
) {
    suspend operator fun invoke(route: Route): Boolean {
        val validation = validator.validate(route)

        if (validation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot create route: invalid route data."
            )
        }

        return routeRepository.create(route)
    }
}
