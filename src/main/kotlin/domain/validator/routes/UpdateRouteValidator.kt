package domain.validator.routes

import domain.model.input.UpdateRouteInput
import domain.validator.ValidationResult

class UpdateRouteValidator {
    fun validate(input: UpdateRouteInput): ValidationResult<RouteValidationError> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(RouteValidationError.NoUpdateFields)
            }

            input.distanceKm?.let { distance ->
                if (distance <= MIN_DISTANCE_KM) add(RouteValidationError.InvalidDistance)
            }

            input.typicalDelayMin?.let { delay ->
                if (delay < MIN_DELAY_MIN) add(RouteValidationError.NegativeDelay)
            }

            val origin = input.originHub
            val destination = input.destinationHub
            if (origin != null && destination != null && origin.id == destination.id) {
                add(RouteValidationError.SameOriginAndDestination)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdateRouteInput): Boolean {
        return input.distanceKm == null &&
                input.typicalDelayMin == null &&
                input.originHub == null &&
                input.destinationHub == null
    }

    companion object{
        const val MIN_DISTANCE_KM = 0.0
        const val MIN_DELAY_MIN = 0
    }
}
