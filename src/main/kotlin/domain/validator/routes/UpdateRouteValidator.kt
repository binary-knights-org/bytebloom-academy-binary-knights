package domain.validator.routes

import domain.model.input.UpdateRouteInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

class UpdateRouteValidator {

    fun validate(input: UpdateRouteInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(
                FieldViolation(
                    field = UpdateRouteInput::id.name,
                    message = "Route ID must not be blank."
                )
            )
        }

        if (hasNoUpdates(input)) {
            violations.add(
                FieldViolation(
                    field = UpdateRouteInput::id.name,
                    message = "At least one field (distanceKm, typicalDelayMin, originHub, destinationHub) must be provided for update."
                )
            )
        }

        input.distanceKm?.let { distance ->
            if (distance <= MIN_DISTANCE_KM) {
                violations.add(
                    FieldViolation(
                        field = UpdateRouteInput::distanceKm.name,
                        message = "Distance must be greater than $MIN_DISTANCE_KM."
                    )
                )
            }
        }

        input.typicalDelayMin?.let { delay ->
            if (delay < MIN_DELAY_MIN) {
                violations.add(
                    FieldViolation(
                        field = UpdateRouteInput::typicalDelayMin.name,
                        message = "Typical delay must be at least $MIN_DELAY_MIN."
                    )
                )
            }
        }

        val origin = input.originHub
        val destination = input.destinationHub
        if (origin != null && destination != null && origin.id == destination.id) {
            violations.add(
                FieldViolation(
                    field = UpdateRouteInput::destinationHub.name,
                    message = "Destination hub must be different from origin hub."
                )
            )
        }

        return violations.toValidationResult()
    }

    private fun hasNoUpdates(input: UpdateRouteInput): Boolean {
        return input.distanceKm == null &&
                input.typicalDelayMin == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
