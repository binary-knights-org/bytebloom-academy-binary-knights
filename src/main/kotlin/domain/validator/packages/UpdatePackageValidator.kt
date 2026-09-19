package domain.validator.packages

import domain.model.input.UpdatePackageInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_WEIGHT = 0.0

class UpdatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: UpdatePackageInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        if (hasNoUpdates(input)) {
            builder.addViolation(
                "updateFields",
                "At least one field (weight, priority, originHub, destinationHub)" +
                        " must be provided for update."
            )
        }

        input.weight?.let { weight ->
            builder.check(
                weight > MIN_WEIGHT,
                "weight",
                "Weight must be greater than $MIN_WEIGHT."
            )
        }

        input.priority?.let { priority ->
            builder.check(
                priority.isNotBlank(),
                "priority",
                "Priority cannot be blank."
            )
        }

        val origin = input.originHub
        val destination = input.destinationHub
        if (origin != null && destination != null) {
            builder.check(
                origin.id != destination.id,
                "destinationHub",
                "Destination hub must be different from origin hub."
            )
        }

        return builder.build()
    }

    private fun hasNoUpdates(input: UpdatePackageInput): Boolean {
        return input.weight == null &&
                input.priority == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
