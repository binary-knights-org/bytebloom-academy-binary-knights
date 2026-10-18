package domain.validator.packages

import domain.model.Package
import domain.model.exception.DomainException
import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.CreatePackageInput
import domain.model.validation.ValidationResult

class CreatePackageValidator {

    fun validate(input: CreatePackageInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (input.weight <= Package.MIN_WEIGHT) {
                add(InvalidPackageWeightException())
            }
            if (input.originHub.id == input.destinationHub.id) {
                add(SameOriginAndDestinationException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
