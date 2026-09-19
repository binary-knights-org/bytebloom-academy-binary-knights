package domain.validator.vehicle

import domain.model.input.CreateVehicleInput
import domain.exception.DomainValidationException
import domain.exception.InvalidCostPerKmException
import domain.exception.InvalidMaxCapacityException
import domain.validator.ValidationResult

private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

class CreateVehicleValidator(
    private val idValidator: VehicleIdValidator
) {
    fun validate(input: CreateVehicleInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainValidationException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (input.maxCapacityKg <= MIN_CAPACITY_KG) {
            errors += InvalidMaxCapacityException(MIN_CAPACITY_KG)
        }

        if (input.costPerKm <= MIN_COST_PER_KM) {
            errors += InvalidCostPerKmException(MIN_COST_PER_KM)
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
