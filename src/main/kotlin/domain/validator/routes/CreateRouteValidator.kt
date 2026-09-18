package domain.validator.routes

import domain.model.Route
import domain.validator.FieldError
import domain.validator.ValidationResult

class CreateRouteValidator(
    private val idValidator: RouteIdValidator
) {

    fun validate(route: Route): ValidationResult {
        val errors = mutableListOf<FieldError>()

        val idResult = idValidator.validate(route.id)

        if (idResult is ValidationResult.Failure) {
            errors.addAll(idResult.errors)
        }

        if (!Route.isValidDistance(route.distanceKm)) {
            errors.add(
                FieldError(
                    "distanceKm",
                    "Distance must be greater than ${Route.MIN_DISTANCE_KM}."
                )
            )
        }

        if (!Route.isValidDelay(route.typicalDelayMin)) {
            errors.add(
                FieldError(
                    "typicalDelayMin",
                    "Typical delay must not be negative."
                )
            )
        }

        if (route.originHub.id.isBlank()) {
            errors.add(
                FieldError(
                    "originHubId",
                    "Hub ID must not be blank."
                )
            )
        }

        if (route.destinationHub.id.isBlank()) {
            errors.add(
                FieldError(
                    "destinationHubId",
                    "Hub ID must not be blank."
                )
            )
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}