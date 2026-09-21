package domain.validator.packages

import domain.model.input.UpdatePackageInput
import domain.validator.ValidationResult

class UpdatePackageValidator {
    fun validate(input: UpdatePackageInput): ValidationResult<PackageValidationError> {
        val violations = buildList {

            if (hasNoUpdates(input)) {
                add(PackageValidationError.NoUpdateFields)
            }

            input.weight?.let { weight ->
                if (weight <= MIN_WEIGHT) {
                    add(PackageValidationError.InvalidWeight)
                }
            }

            val origin = input.originHub
            val destination = input.destinationHub
            if (origin != null && destination != null && origin.id == destination.id) {
                add(PackageValidationError.SameOriginAndDestination)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdatePackageInput): Boolean {
        return input.weight == null &&
                input.priority == null &&
                input.originHub == null &&
                input.destinationHub == null
    }

    companion object{
        const val MIN_WEIGHT = 0.0
    }
}
