package domain.usecase.crud.route

import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.RouteCreateFields
import domain.validator.RouteValidator
import domain.validator.ValidationResult

class CreateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(route: Route): Boolean {
        val fields = RouteCreateFields(
            routeId = route.id,
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin,
            originHubId = route.originHub.id,
            destinationHubId = route.destinationHub.id
        )

        val isValid = RouteValidator.validateForCreate(fields) is ValidationResult.Success
        return isValid && routeRepository.create(route)
    }
}
