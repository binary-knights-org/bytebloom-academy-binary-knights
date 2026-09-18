package domain.usecase.crud.route

import domain.exception.EntityValidationException
import domain.model.Package
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
        val idValidation = idValidator.validate(input.id)
        if (idValidation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot update route: invalid route ID."
            )
        }

        val fieldValidation = validator.validate(input)
        if (fieldValidation is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update package: invalid package data.")
        }

        val existingPkg = routeRepository.getById(input.id)
            ?: throw EntityValidationException("Route with ID '${input.id}' was not found.")


        val updatedPkg = Route.create(
            id = existingPkg.id,
            distanceKm = input.distanceKm ?: existingPkg.distanceKm,
            typicalDelayMin = input.typicalDelayMin ?: existingPkg.typicalDelayMin,
            originHub = input.originHub ?: existingPkg.originHub,
            destinationHub = input.destinationHub ?: existingPkg.destinationHub
        )

        return routeRepository.update(updatedPkg)
    }
}
