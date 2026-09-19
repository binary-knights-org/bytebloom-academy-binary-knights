package domain.validator.routes

import domain.model.input.UpdateRouteInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

class UpdateRouteValidator(
    private val idValidator: RouteIdValidator
) {
    fun validate(input: UpdateRouteInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        if (hasNoUpdates(input)) {
            builder.addViolation("updateFields",
                "At least one field (distanceKm, typicalDelayMin, originHub, destinationHub)" +
                        " must be provided for update.")
        }

        input.distanceKm?.let { distance ->
            builder.check(distance > MIN_DISTANCE_KM, "distanceKm",
                "Distance must be greater than $MIN_DISTANCE_KM.")
        }

        input.typicalDelayMin?.let { delay ->
            builder.check(delay >= MIN_DELAY_MIN, "typicalDelayMin",
                "Typical delay must be at least $MIN_DELAY_MIN.")
        }

        val origin = input.originHub
        val destination = input.destinationHub
        if (origin != null && destination != null) {
            builder.check(origin.id != destination.id, "destinationHub",
                "Destination hub must be different from origin hub.")
        }

        return builder.build()
    }

    private fun hasNoUpdates(input: UpdateRouteInput): Boolean {
        return input.distanceKm == null &&
                input.typicalDelayMin == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
