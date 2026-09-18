package domain.validator.routes

import domain.model.Route
import domain.model.Warehouse
import domain.model.input.UpdateRouteInput
import domain.validator.FieldError
import domain.validator.ValidationResult

class UpdateRouteValidator {

    fun validate(
        input: UpdateRouteInput
    ): ValidationResult {

        val errors = mutableListOf<FieldError>()

        if (hasNoFieldsToUpdate(input)) {
            errors.add(
                FieldError(
                    "update", "At least one field must be provided for update."
                )
            )
        }

        input.distanceKm?.let {
            if (!Route.isValidDistance(it)) {
                errors.add(
                    FieldError(
                        "distanceKm", "Distance must be greater than ${Route.MIN_DISTANCE_KM}."
                    )
                )
            }
        }

        input.typicalDelayMin?.let {
            if (!Route.isValidDelay(it)) {
                errors.add(
                    FieldError(
                        "typicalDelayMin", "Typical delay must not be negative."
                    )
                )
            }
        }

        input.originHub?.let {
            if (it.id.isBlank()) {
                errors.add(
                    FieldError(
                        "originHubId", "Hub ID must not be blank."
                    )
                )
            }
        }

        input.destinationHub?.let {
            if (it.id.isBlank()) {
                errors.add(
                    FieldError(
                        "destinationHubId", "Hub ID must not be blank."
                    )
                )
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}
    private fun hasNoFieldsToUpdate(input: UpdateRouteInput): Boolean =
        listOfNotNull(
            input.distanceKm,
            input.typicalDelayMin,
            input.originHub,
            input.destinationHub,
        ).isEmpty()

