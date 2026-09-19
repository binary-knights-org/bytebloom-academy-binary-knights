package domain.validator.packages

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val PACKAGE_ID_PREFIX = "PKG-"

class PackageIdValidator {

    fun validate(id: String): ValidationResult<Unit> {
        val errors = IdValidator.validate(id, PACKAGE_ID_PREFIX, "Package")
        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
