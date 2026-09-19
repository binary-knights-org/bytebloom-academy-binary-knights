package domain.usecase.crud.route

import domain.model.Route
import domain.model.input.UpdateRouteInput
import domain.repository.RouteRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.model.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository, private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: UpdateRouteInput): ValidationResult<Route> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        return executeUpdate(input)
    }

    private suspend fun executeUpdate(input: UpdateRouteInput): ValidationResult<Route> {
        val existingRoute = routeRepository.getById(input.id) ?: return ValidationResult.Failure(
            listOf(EntityNotFoundException("Route", input.id))
        )

        val updatedRoute = existingRoute.copy(
            distanceKm = input.distanceKm ?: existingRoute.distanceKm,
            typicalDelayMin = input.typicalDelayMin ?: existingRoute.typicalDelayMin,
            originHub = input.originHub ?: existingRoute.originHub,
            destinationHub = input.destinationHub ?: existingRoute.destinationHub
        )

        val isUpdated = routeRepository.update(updatedRoute)

        return if (isUpdated) ValidationResult.Success(updatedRoute)
        else ValidationResult.Failure(listOf(DatabaseOperationFailedException("update", "route")))
    }
}
