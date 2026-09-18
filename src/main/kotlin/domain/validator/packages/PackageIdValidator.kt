package domain.validator.packages

import domain.model.Package
import domain.validator.FieldError
import domain.validator.ValidationResult

class PackageIdValidator {

    fun validate(id: String): ValidationResult {
        return if (Package.isValidId(id)) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(
                listOf(
                    FieldError(
                        "id",
                        "Package ID must start with '${Package.ID_PREFIX}' or be a valid UUID."
                    )
                )
            )
        }
    }
}
