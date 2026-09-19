package domain.validator.vehicle

import domain.model.input.UpdateVehicleInput
import domain.exception.DomainValidationException
import domain.exception.InvalidCostPerKmException
import domain.exception.InvalidMaxCapacityException
import domain.exception.NoUpdateFieldsException
import domain.validator.ValidationResult

private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

class UpdateVehicleValidator(
    private val idValidator: VehicleIdValidator
) {
    fun validate(input: UpdateVehicleInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainValidationException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (hasNoUpdates(input)) {
            errors += NoUpdateFieldsException("maxCapacityKg, costPerKm, currentHub")
        }

        input.maxCapacityKg?.let { capacity ->
            if (capacity <= MIN_CAPACITY_KG) {
                errors += InvalidMaxCapacityException(MIN_CAPACITY_KG)
            }
        }

        input.costPerKm?.let { cost ->
            if (cost <= MIN_COST_PER_KM) {
                errors += InvalidCostPerKmException(MIN_COST_PER_KM)
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }

    private fun hasNoUpdates(input: UpdateVehicleInput): Boolean {
        return input.maxCapacityKg == null &&
                input.costPerKm == null &&
                input.currentHub == null
    }
}
