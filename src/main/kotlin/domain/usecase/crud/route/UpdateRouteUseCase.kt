package domain.usecase.crud.route

import domain.exception.ResourceNotFoundException
import domain.model.input.UpdateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: UpdateRouteInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation.isInvalid) return validation

        val existingRoute = routeRepository.getById(input.id)
            ?: throw ResourceNotFoundException("Route with ID '${input.id}' was not found.")

        val updatedRoute = existingRoute.copy(
            distanceKm = input.distanceKm ?: existingRoute.distanceKm,
            typicalDelayMin = input.typicalDelayMin ?: existingRoute.typicalDelayMin,
            originHub = input.originHub ?: existingRoute.originHub,
            destinationHub = input.destinationHub ?: existingRoute.destinationHub
        )

        routeRepository.update(updatedRoute)

        return ValidationResult.Valid
    }
}
