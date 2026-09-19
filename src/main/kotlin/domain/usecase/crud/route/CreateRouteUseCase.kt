package domain.usecase.crud.route

import domain.model.Route
import domain.model.input.CreateRouteInput
import domain.repository.RouteRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.routes.CreateRouteValidator

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator
) {
    suspend operator fun invoke(input: CreateRouteInput): ValidationResult<Route> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val route = Route(
            id = input.id,
            distanceKm = input.distanceKm,
            typicalDelayMin = input.typicalDelayMin,
            originHub = input.originHub,
            destinationHub = input.destinationHub
        )

        val isCreated = routeRepository.create(route)

        return if (isCreated) {
            ValidationResult.Success(route)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("create", "route")))
        }
    }
}
