package domain.validator.packages

import domain.model.input.UpdatePackageInput
import domain.exception.DomainValidationException
import domain.exception.InvalidWeightException
import domain.exception.NoUpdateFieldsException
import domain.exception.SameOriginDestinationException
import domain.validator.ValidationResult

private const val MIN_WEIGHT = 0.0

class UpdatePackageValidator(
    private val idValidator: PackageIdValidator
) {
    fun validate(input: UpdatePackageInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainValidationException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (hasNoUpdates(input)) {
            errors += NoUpdateFieldsException("weight, priority, originHub, destinationHub")
        }

        input.weight?.let { weight ->
            if (weight <= MIN_WEIGHT) {
                errors += InvalidWeightException(MIN_WEIGHT)
            }
        }

        val origin = input.originHub
        val destination = input.destinationHub
        if (origin != null && destination != null && origin.id == destination.id) {
            errors += SameOriginDestinationException()
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }

    private fun hasNoUpdates(input: UpdatePackageInput): Boolean {
        return input.weight == null &&
                input.priority == null &&
                input.originHub == null &&
                input.destinationHub == null
    }
}
