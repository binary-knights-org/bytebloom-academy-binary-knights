package domain.validator.packages

import domain.model.input.UpdatePackageInput
import domain.model.Package
import domain.validator.FieldViolation
import domain.validator.ValidationResult

class UpdatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: UpdatePackageInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) violations.addAll(idResult.violations)

        if (input.weight == null && input.priority == null && input.originHub == null && input.destinationHub == null) {
            violations.add(FieldViolation("update", "At least one target property must be populated for update."))
        }

        input.weight?.let {
            if (!Package.isValidWeight(it)) {
                violations.add(FieldViolation("weight", "Weight must be greater than ${Package.MIN_WEIGHT}."))
            }
        }

        input.priority?.let {
            if (!Package.isValidPriority(it)) {
                violations.add(FieldViolation("priority", "Priority must be one of: ${Package.ALLOWED_PRIORITIES.joinToString(", ")}."))
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
