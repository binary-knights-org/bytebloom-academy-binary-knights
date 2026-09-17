package domain.validator.packages

import domain.model.Package
import domain.validator.FieldViolation
import domain.validator.ValidationResult

class PackageIdValidator {
    fun validate(id: String): ValidationResult {
        return if (Package.isValidId(id)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                listOf(FieldViolation("id", "Package ID must start with '${Package.ID_PREFIX}' or be a valid UUID."))
            )
        }
    }
}
