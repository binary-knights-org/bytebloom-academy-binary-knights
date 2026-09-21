package domain.validator.vehicle

import domain.model.input.UpdateVehicleInput
import domain.validator.ValidationResult

class UpdateVehicleValidator {
    fun validate(input: UpdateVehicleInput): ValidationResult<VehicleValidationError> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(VehicleValidationError.NoUpdateFields)
            }

            input.maxCapacityKg?.let { capacity ->
                if (capacity <= MIN_CAPACITY_KG) add(VehicleValidationError.InvalidCapacity)
            }

            input.costPerKm?.let { cost ->
                if (cost <= MIN_COST_PER_KM) add(VehicleValidationError.InvalidCost)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdateVehicleInput): Boolean {
        return input.maxCapacityKg == null &&
                input.costPerKm == null &&
                input.currentHub == null
    }

    companion object{
        const val MIN_CAPACITY_KG = 0.0
        const val MIN_COST_PER_KM = 0.0
    }
}
