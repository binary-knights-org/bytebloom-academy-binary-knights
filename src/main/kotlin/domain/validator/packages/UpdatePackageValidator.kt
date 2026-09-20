package domain.validator.packages

import domain.model.input.UpdatePackageInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_WEIGHT = 0.0

class UpdatePackageValidator {

    fun validate(input: UpdatePackageInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(
                FieldViolation(
                    field = UpdatePackageInput::id.name,
                    message = "Package ID must not be blank."
                )
            )
        }

        if (hasNoUpdates(input)) {
            violations.add(
                FieldViolation(
                    field = UpdatePackageInput::id.name,
                    message = "At least one field (weight, priority, originHub, destinationHub) must be provided for update."
                )
            )
        }

        input.weight?.let { weight ->
            if (weight <= MIN_WEIGHT) {
                violations.add(
                    FieldViolation(
                        field = UpdatePackageInput::weight.name,
                        message = "Weight must be greater than $MIN_WEIGHT."
                    )
                )
            }
        }

        input.priority?.let { priority ->
            if (priority.isBlank()) {
                violations.add(
                    FieldViolation(
                        field = UpdatePackageInput::priority.name,
                        message = "Priority cannot be blank."
                    )
                )
            }
        }

        val origin = input.originHub
        val destination = input.destinationHub
        if (origin != null && destination != null && origin.id == destination.id) {
            violations.add(
                FieldViolation(
                    field = UpdatePackageInput::destinationHub.name,
                    message = "Destination hub must be different from origin hub."
                )
            )
        }

        return violations.toValidationResult()
    }

    private fun hasNoUpdates(input: UpdatePackageInput): Boolean {
        return input.weight == null &&
                input.priority == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
