package domain.validator.packages

import domain.model.input.CreatePackageInput
import domain.validator.ValidationResult

class CreatePackageValidator {

    fun validate(
        input: CreatePackageInput
    ): ValidationResult<PackageValidationError> {

        val violations = buildList {

            if (input.weight <= MIN_WEIGHT)
                add(PackageValidationError.InvalidWeight)

            if (input.originHub.id == input.destinationHub.id)
                add(PackageValidationError.SameOriginAndDestination)
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    companion object {
        const val MIN_WEIGHT = 0.0
    }
}
