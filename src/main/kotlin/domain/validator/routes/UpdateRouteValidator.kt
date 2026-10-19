package domain.validator.routes

import domain.model.exception.DomainException
import domain.model.exception.InvalidRouteDelayException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.NoUpdateFieldsException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.UpdateRouteInput
import domain.model.validation.ValidationResult

class UpdateRouteValidator {

    fun validate(input: UpdateRouteInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(NoUpdateFieldsException())
            }

            input.distanceKm?.let { distance ->
                if (distance <= MIN_DISTANCE_KM) add(InvalidRouteDistanceException())
            }

            input.typicalDelayMin?.let { delay ->
                if (delay < MIN_DELAY_MIN) add(InvalidRouteDelayException())
            }

            val origin = input.originHub
            val destination = input.destinationHub
            if (origin != null && destination != null && origin.id == destination.id) {
                add(SameOriginAndDestinationException())
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

    private companion object {
        private const val MIN_DISTANCE_KM = 0.0
        private const val MIN_DELAY_MIN = 0
    }
}
