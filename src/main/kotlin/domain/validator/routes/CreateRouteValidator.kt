package domain.validator.routes

import domain.model.input.CreateRouteInput
import domain.model.exception.DomainException
import domain.model.exception.InvalidDelayException
import domain.model.exception.InvalidDistanceException
import domain.model.exception.SameOriginDestinationException
import domain.validator.ValidationResult

private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

class CreateRouteValidator(
    private val idValidator: RouteIdValidator
) {
    fun validate(input: CreateRouteInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (input.distanceKm <= MIN_DISTANCE_KM) {
            errors += InvalidDistanceException(MIN_DISTANCE_KM)
        }

        if (input.typicalDelayMin < MIN_DELAY_MIN) {
            errors += InvalidDelayException(MIN_DELAY_MIN)
        }

        if (input.originHub.id == input.destinationHub.id) {
            errors += SameOriginDestinationException()
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
