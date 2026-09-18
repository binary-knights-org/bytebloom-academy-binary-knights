package domain.validator.routes

import domain.model.Route
import domain.model.Warehouse
import domain.validator.FieldError
import domain.validator.ValidationResult

class UpdateRouteValidator {

    fun validate(
        distanceKm: Double? = null,
        typicalDelayMin: Int? = null,
        destinationHub: Warehouse? = null
    ): ValidationResult {

        val errors = mutableListOf<FieldError>()

        if (distanceKm == null &&
            typicalDelayMin == null &&
            destinationHub == null
        ) {
            errors.add(
                FieldError(
                    "update",
                    "At least one field must be provided for update."
                )
            )
        }

        distanceKm?.let {
            if (!Route.isValidDistance(it)) {
                errors.add(
                    FieldError(
                        "distanceKm",
                        "Distance must be greater than ${Route.MIN_DISTANCE_KM}."
                    )
                )
            }
        }

        typicalDelayMin?.let {
            if (!Route.isValidDelay(it)) {
                errors.add(
                    FieldError(
                        "typicalDelayMin",
                        "Typical delay must not be negative."
                    )
                )
            }
        }

        destinationHub?.let {
            if (it.id.isBlank()) {
                errors.add(
                    FieldError(
                        "destinationHubId",
                        "Hub ID must not be blank."
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
