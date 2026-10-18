package domain.usecase.crud.route

import domain.model.Route
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdateRouteInput
import domain.model.validation.ValidationResult
import domain.repository.RouteRepository
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: UpdateRouteInput): Result<Route> {
        val validation = validator.validate(input)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                EntityValidationException(validation.violations)
            )
        }

        return runCatching {
            val existing = routeRepository.getById(input.id)
                ?: throw ResourceNotFoundException()

            val updated = existing.copy(
                distanceKm = input.distanceKm ?: existing.distanceKm,
                typicalDelayMin = input.typicalDelayMin
                    ?: existing.typicalDelayMin,
                originHub = input.originHub ?: existing.originHub,
                destinationHub = input.destinationHub
                    ?: existing.destinationHub
            )

            routeRepository.update(updated)
            updated
        }
    }
}
