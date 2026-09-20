package domain.validator.packages

import domain.model.input.CreatePackageInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_WEIGHT = 0.0

class CreatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: CreatePackageInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        builder.check(
            input.weight > MIN_WEIGHT,
            "weight",
            "Weight must be greater than $MIN_WEIGHT."
        )
        builder.check(
            input.priority.isNotBlank(),
            "priority",
            "Priority cannot be blank."
        )
        builder.check(
            input.originHub.id != input.destinationHub.id,
            "destinationHub",
            "Destination hub must be different from origin hub."
        )

        return builder.build()
    }
}
