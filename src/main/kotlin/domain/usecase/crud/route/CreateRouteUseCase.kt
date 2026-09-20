package domain.usecase.crud.routes

import domain.model.Route
import domain.model.input.CreateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.CreateRouteValidator

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator
) {
    suspend operator fun invoke(input: CreateRouteInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid) return validation

        val route = Route(
            id = input.id,
            distanceKm = input.distanceKm,
            typicalDelayMin = input.typicalDelayMin,
            originHub = input.originHub,
            destinationHub = input.destinationHub
        )

        routeRepository.create(route)

        return ValidationResult.Valid
    }
}
