package domain.validator.vehicle

import domain.model.input.CreateVehicleInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

class CreateVehicleValidator
 {
    fun validate(input: CreateVehicleInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()


        if (input.maxCapacityKg <= MIN_CAPACITY_KG) {
            violations.add(
                FieldViolation(
                    CreateVehicleInput::maxCapacityKg.name,
                    "Max capacity must be greater than $MIN_CAPACITY_KG."
                )
            )
        }
        if (input.costPerKm <= MIN_COST_PER_KM) {
            violations.add(
                FieldViolation(
                    CreateVehicleInput::costPerKm.name,
                    "Cost per km must be greater than $MIN_COST_PER_KM."
                )
            )
        }

        return violations.toValidationResult()
    }
}
