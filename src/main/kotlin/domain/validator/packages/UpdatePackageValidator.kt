package domain.validator.packages

import domain.model.input.UpdatePackageInput
import domain.model.Package
import domain.model.Warehouse
import domain.validator.FieldError
import domain.validator.ValidationResult

class UpdatePackageValidator {

    fun validate(
        input: UpdatePackageInput,
    ): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (input.weight == null && input.priority == null && input.originHub == null && input.destinationHub == null) {
            errors.add(
                FieldError(
                    "update", "At least one target property must be populated for update."
                )
            )
        }

        input.weight?.let {
            if (!Package.isValidWeight(it)) {
                errors.add(
                    FieldError(
                        "weight", "Weight must be greater than ${Package.MIN_WEIGHT}."
                    )
                )
            }
        }

        input.priority?.let {
            if (!Package.isValidPriority(it)) {
                errors.add(
                    FieldError(
                        "priority",
                        "Priority must be one of: ${Package.ALLOWED_PRIORITIES.joinToString(", ")}."
                    )
                )
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }
}
