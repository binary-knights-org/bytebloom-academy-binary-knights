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
        val idValidation = RouteValidator.validateId(route.id)
        if (idValidation is ValidationResult.Failure) {
            return false
        }

        val fields = RouteUpdateFields(
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin,
            destinationHubId = route.destinationHub.id
        )

        val fieldsValidation = RouteValidator.validateForUpdate(fields)
        if (fieldsValidation is ValidationResult.Failure) {
            return false
        }

        return routeRepository.updateRoute(route)
    }
}
