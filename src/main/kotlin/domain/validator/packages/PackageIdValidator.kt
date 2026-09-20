package domain.validator.packages

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val PACKAGE_ID_PREFIX = "PKG-"

class PackageIdValidator {
    fun validate(id: String): ValidationResult {
        val violations = IdValidator.validate(id, PACKAGE_ID_PREFIX, "Package")
        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
