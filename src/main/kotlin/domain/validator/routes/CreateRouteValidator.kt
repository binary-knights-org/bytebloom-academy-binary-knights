package domain.validator.routes

import domain.model.exception.DomainException
import domain.model.exception.InvalidRouteDelayException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.CreateRouteInput
import domain.model.validation.ValidationResult

class CreateRouteValidator {

    fun validate(input: CreateRouteInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (input.distanceKm <= MIN_DISTANCE_KM) {
                add(InvalidRouteDistanceException())
            }
            if (input.typicalDelayMin < MIN_DELAY_MIN) {
                add(InvalidRouteDelayException())
            }
            if (input.originHub.id == input.destinationHub.id) {
                add(SameOriginAndDestinationException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private companion object {
        private const val MIN_DISTANCE_KM = 0.0
        private const val MIN_DELAY_MIN = 0
    }
}
