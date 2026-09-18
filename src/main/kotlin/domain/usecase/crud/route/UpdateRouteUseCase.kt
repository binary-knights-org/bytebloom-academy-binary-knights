package domain.usecase.crud.route

import domain.exception.EntityValidationException
import domain.model.Route
import domain.model.input.UpdateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.RouteIdValidator
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: UpdateRouteInput): Boolean {
        validateInput(input)

        val existingRoute = routeRepository.getById(input.id)
            ?: throw EntityValidationException("Route with ID '${input.id}' was not found.")

        val updatedRoute = Route.create(
            id = existingRoute.id,
            distanceKm = input.distanceKm ?: existingRoute.distanceKm,
            typicalDelayMin = input.typicalDelayMin ?: existingRoute.typicalDelayMin,
            originHub = input.originHub ?: existingRoute.originHub,
            destinationHub = input.destinationHub ?: existingRoute.destinationHub
        )

        return routeRepository.update(updatedRoute)
    }

    private fun validateInput(input: UpdateRouteInput) {
        if (idValidator.validate(input.id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update route: invalid route ID.")
        }

        if (validator.validate(input) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update route: invalid route data.")
        }
    }
}
