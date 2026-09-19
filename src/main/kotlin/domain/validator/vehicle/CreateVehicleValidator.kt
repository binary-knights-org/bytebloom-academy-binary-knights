package domain.validator.vehicle

import domain.model.input.CreateVehicleInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

class CreateVehicleValidator(
    private val idValidator: VehicleIdValidator
) {
    fun validate(input: CreateVehicleInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        builder.check(
            input.maxCapacityKg > MIN_CAPACITY_KG,
            "maxCapacityKg",
            "Max capacity must be greater than $MIN_CAPACITY_KG."
        )
        builder.check(
            input.costPerKm > MIN_COST_PER_KM,
            "costPerKm",
            "Cost per km must be greater than $MIN_COST_PER_KM."
        )

        return builder.build()
    }
}
