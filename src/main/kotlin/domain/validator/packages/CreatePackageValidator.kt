package domain.validator.packages

import domain.model.input.CreatePackageInput
import domain.model.exception.DomainException
import domain.model.exception.InvalidWeightException
import domain.model.exception.SameOriginDestinationException
import domain.validator.ValidationResult

private const val MIN_WEIGHT = 0.0

class CreatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: CreatePackageInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainException>()

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
