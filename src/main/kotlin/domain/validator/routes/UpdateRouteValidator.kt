package domain.validator.routes

import domain.model.input.UpdateRouteInput
import domain.exception.DomainValidationException
import domain.exception.InvalidDelayException
import domain.exception.InvalidDistanceException
import domain.exception.NoUpdateFieldsException
import domain.exception.SameOriginDestinationException
import domain.validator.ValidationResult

private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

class UpdateRouteValidator(
    private val idValidator: RouteIdValidator
) {
    fun validate(input: UpdateRouteInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainValidationException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (hasNoUpdates(input)) {
            errors += NoUpdateFieldsException("distanceKm, typicalDelayMin, originHub, destinationHub")
        }

        input.distanceKm?.let { distance ->
            if (distance <= MIN_DISTANCE_KM) {
                errors += InvalidDistanceException(MIN_DISTANCE_KM)
            }
        }

        input.typicalDelayMin?.let { delay ->
            if (delay < MIN_DELAY_MIN) {
                errors += InvalidDelayException(MIN_DELAY_MIN)
            }
        }

        val origin = input.originHub
        val destination = input.destinationHub
        if (origin != null && destination != null && origin.id == destination.id) {
            errors += SameOriginDestinationException()
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }

    private fun hasNoUpdates(input: UpdateRouteInput): Boolean {
        return input.distanceKm == null &&
                input.typicalDelayMin == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
