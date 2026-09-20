package domain.validator.routes

import domain.model.input.CreateRouteInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

class CreateRouteValidator(
    private val idValidator: RouteIdValidator
) {
    fun validate(input: CreateRouteInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        builder.check(
            input.distanceKm > MIN_DISTANCE_KM,
            "distanceKm",
            "Distance must be greater than $MIN_DISTANCE_KM."
        )
        builder.check(
            input.typicalDelayMin >= MIN_DELAY_MIN,
            "typicalDelayMin",
            "Typical delay must be at least $MIN_DELAY_MIN."
        )
        builder.check(
            input.originHub.id != input.destinationHub.id,
            "destinationHub",
            "Destination hub must be different from origin hub."
        )

        return builder.build()
    }
}
