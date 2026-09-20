package domain.validator.routes

import domain.model.input.CreateRouteInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

class CreateRouteValidator(
) {
    fun validate(input: CreateRouteInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.distanceKm < MIN_DISTANCE_KM)
           {  violations.add(
               FieldViolation(
                   CreateRouteInput::distanceKm.name,
                   "Distance must be greater than $MIN_DISTANCE_KM."
               )
           )
        }

        if (input.typicalDelayMin <= MIN_DELAY_MIN)
            {  violations.add(
                FieldViolation(
                    CreateRouteInput::typicalDelayMin.name,
                    "Typical delay must be at least $MIN_DELAY_MIN."
                )
            )
        }

        if (input.originHub.id == input.destinationHub.id)
            {  violations.add(
                FieldViolation(
                    CreateRouteInput::destinationHub.name,
                    "Destination hub must be different from origin hub."
                )
            )
        }
        return violations.toValidationResult()
    }
}
