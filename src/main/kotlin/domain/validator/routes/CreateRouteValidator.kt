package domain.validator.routes

import domain.model.input.CreateRouteInput
import domain.validator.ValidationResult

class CreateRouteValidator {
    fun validate(input: CreateRouteInput): ValidationResult<RouteValidationError> {
        val violations = buildList {
            if (input.distanceKm <= MIN_DISTANCE_KM) {
                add(RouteValidationError.InvalidDistance)
            }
            if (input.typicalDelayMin < MIN_DELAY_MIN) {
                add(RouteValidationError.NegativeDelay)
            }
            if (input.originHub.id == input.destinationHub.id) {
                add(RouteValidationError.SameOriginAndDestination)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
    companion object{
        const val MIN_DISTANCE_KM = 0.0
        const val MIN_DELAY_MIN = 0
    }
}
