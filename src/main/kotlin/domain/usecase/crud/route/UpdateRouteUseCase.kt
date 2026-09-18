package domain.usecase.crud.route

import domain.exception.EntityValidationException
import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.RouteIdValidator
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(route: Route): Boolean {
        val idValidation = idValidator.validate(route.id)

        if (idValidation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot update route: invalid route ID."
            )
        }

        val fieldValidation = validator.validate(
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin,
            destinationHub = route.destinationHub
        )

        if (fieldValidation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot update route: invalid route data."
            )
        }

        return routeRepository.update(route)
    }
}
