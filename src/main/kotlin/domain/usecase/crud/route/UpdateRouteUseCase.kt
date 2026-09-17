package domain.usecase.crud.route

import domain.model.Route
import domain.repository.RouteRepository
import domain.validator.RouteUpdateFields
import domain.validator.RouteValidator
import domain.validator.ValidationResult

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(route: Route): Boolean {
        val fields = RouteUpdateFields(
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin,
            destinationHubId = route.destinationHub.id
        )

        val isValid = RouteValidator.validateId(route.id) is ValidationResult.Success &&
                RouteValidator.validateForUpdate(fields) is ValidationResult.Success

        return isValid && routeRepository.update(route)
    }
}
