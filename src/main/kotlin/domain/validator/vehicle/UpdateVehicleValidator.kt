package domain.validator.vehicle

import domain.model.input.UpdateVehicleInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

class UpdateVehicleValidator {

    fun validate(input: UpdateVehicleInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(
                FieldViolation(
                    field = UpdateVehicleInput::id.name,
                    message = "Vehicle ID must not be blank."
                )
            )
        }

        if (hasNoUpdates(input)) {
            violations.add(
                FieldViolation(
                    field = UpdateVehicleInput::id.name,
                    message = "At least one field (maxCapacityKg, costPerKm, currentHub) must be provided for update."
                )
            )
        }

        input.maxCapacityKg?.let { capacity ->
            if (capacity <= MIN_CAPACITY_KG) {
                violations.add(
                    FieldViolation(
                        field = UpdateVehicleInput::maxCapacityKg.name,
                        message = "Max capacity must be greater than $MIN_CAPACITY_KG."
                    )
                )
            }
        }

        input.costPerKm?.let { cost ->
            if (cost <= MIN_COST_PER_KM) {
                violations.add(
                    FieldViolation(
                        field = UpdateVehicleInput::costPerKm.name,
                        message = "Cost per km must be greater than $MIN_COST_PER_KM."
                    )
                )
            }
        }

        return violations.toValidationResult()
    }

    private fun hasNoUpdates(input: UpdateVehicleInput): Boolean {
        return input.maxCapacityKg == null &&
                input.costPerKm == null &&
                input.currentHub == null
    }
}
