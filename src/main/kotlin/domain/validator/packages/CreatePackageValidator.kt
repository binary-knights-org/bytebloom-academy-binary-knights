package domain.validator.packages

import domain.model.input.CreatePackageInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_WEIGHT = 0.0

class CreatePackageValidator(
) {
    fun validate(input: CreatePackageInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.weight <= MIN_WEIGHT) {
            violations.add(
                FieldViolation(
                    CreatePackageInput::weight.name,
                    message = "Weight must be greater than $MIN_WEIGHT."
                )
            )
        }

        if ( input.priority.isBlank()) {
            violations.add(
                FieldViolation(
                    CreatePackageInput::priority.name,
                    message ="Priority cannot be blank."

                )
            )
        }

        if ( input.originHub.id == input.destinationHub.id){
            violations.add(
                FieldViolation(
                    CreatePackageInput::destinationHub.name,
                    message ="Destination hub must be different from origin hub."
                )
            )
        }

        return violations.toValidationResult()
    }
}
