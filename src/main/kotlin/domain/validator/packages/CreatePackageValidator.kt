package domain.validator.packages

import domain.model.Package
import domain.validator.FieldError
import domain.validator.ValidationResult

class CreatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(pkg: Package): ValidationResult {
        val errors = mutableListOf<FieldError>()

        val idResult = idValidator.validate(pkg.id)

        if (idResult is ValidationResult.Failure)
            errors.addAll(idResult.errors)

        if (!Package.isValidWeight(pkg.weight)) {
            errors.add(
                FieldError(
                    "weight",
                    "Weight must be greater than ${Package.MIN_WEIGHT}."
                )
            )
        }

        if (!Package.isValidPriority(pkg.priority)) {
            errors.add(
                FieldError(
                    "priority",
                    "Priority must be one of: ${Package.ALLOWED_PRIORITIES.joinToString(", ")}."
                )
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }
}
