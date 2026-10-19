package domain.validator.packages

import domain.model.Package
import domain.model.exception.DomainException
import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.NoUpdateFieldsException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.UpdatePackageInput
import domain.model.validation.ValidationResult

class UpdatePackageValidator {

    fun validate(input: UpdatePackageInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(NoUpdateFieldsException())
            }

            input.weight?.let { weight ->
                if (weight <= Package.MIN_WEIGHT) {
                    add(InvalidPackageWeightException())
                }
            }

            val origin = input.originHub
            val destination = input.destinationHub
            if (origin != null && destination != null && origin.id == destination.id) {
                add(SameOriginAndDestinationException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdatePackageInput): Boolean {
        return input.weight == null &&
                input.priority == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
