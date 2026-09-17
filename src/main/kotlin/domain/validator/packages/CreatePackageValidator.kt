package domain.validator.packages

import domain.model.input.CreatePackageInput
import domain.model.Package
import domain.validator.FieldViolation
import domain.validator.ValidationResult

class CreatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: CreatePackageInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) violations.addAll(idResult.violations)

        if (!Package.isValidWeight(input.weight)) {
            violations.add(FieldViolation("weight", "Weight must be greater than ${Package.MIN_WEIGHT}."))
        }

        if (!Package.isValidPriority(input.priority)) {
            violations.add(FieldViolation("priority", "Priority must be one of: ${Package.ALLOWED_PRIORITIES.joinToString(", ")}."))
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
