package domain.validator.packages

import domain.model.input.CreatePackageInput
import domain.exception.DomainValidationException
import domain.exception.InvalidWeightException
import domain.exception.SameOriginDestinationException
import domain.validator.ValidationResult

private const val MIN_WEIGHT = 0.0

class CreatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: CreatePackageInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainValidationException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (input.weight <= MIN_WEIGHT) {
            errors += InvalidWeightException(MIN_WEIGHT)
        }

        if (input.originHub.id == input.destinationHub.id) {
            errors += SameOriginDestinationException()
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
