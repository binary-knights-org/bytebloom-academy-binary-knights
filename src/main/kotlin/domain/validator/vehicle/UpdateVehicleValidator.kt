package domain.validator.vehicle

import domain.model.input.UpdateVehicleInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

class UpdateVehicleValidator(
    private val idValidator: VehicleIdValidator
) {
    fun validate(input: UpdateVehicleInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        if (hasNoUpdates(input)) {
            builder.addViolation("updateFields",
                "At least one field (maxCapacityKg, costPerKm, currentHub) " +
                        "must be provided for update.")
        }

        input.maxCapacityKg?.let { capacity ->
            builder.check(capacity > MIN_CAPACITY_KG, "maxCapacityKg",
                "Max capacity must be greater than $MIN_CAPACITY_KG.")
        }

        input.costPerKm?.let { cost ->
            builder.check(cost > MIN_COST_PER_KM, "costPerKm",
                "Cost per km must be greater than $MIN_COST_PER_KM.")
        }

        return builder.build()
    }

    private fun hasNoUpdates(input: UpdateVehicleInput): Boolean {
        return input.maxCapacityKg == null &&
                input.costPerKm == null &&
                input.currentHub == null
    }
}
