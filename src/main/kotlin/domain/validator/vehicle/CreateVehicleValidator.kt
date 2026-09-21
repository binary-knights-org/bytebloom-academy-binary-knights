package domain.validator.vehicle

import domain.model.input.CreateVehicleInput
import domain.validator.ValidationResult

class CreateVehicleValidator {
    fun validate(input: CreateVehicleInput): ValidationResult<VehicleValidationError> {
        val violations = buildList {
            if (input.maxCapacityKg <= MIN_CAPACITY_KG) {
                add(VehicleValidationError.InvalidCapacity)
            }
            if (input.costPerKm <= MIN_COST_PER_KM) {
                add(VehicleValidationError.InvalidCost)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    companion object{
        const val MIN_CAPACITY_KG = 0.0
        const val MIN_COST_PER_KM = 0.0
    }
}
