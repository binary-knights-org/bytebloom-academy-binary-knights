package domain.validator.vehicle

import domain.model.Vehicle
import domain.model.input.UpdateVehicleInput
import domain.model.input.UpdateWarehouseInput
import domain.validator.FieldError
import domain.validator.ValidationResult

class UpdateVehicleValidator {

    fun validate(
        input: UpdateVehicleInput
    ): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (hasNoFieldsToUpdate(input)) {
            errors.add(
                FieldError(
                    "update", "At least one field must be provided for update."
                )
            )
        }

        input.maxCapacityKg?.let {
            if (!Vehicle.isValidCapacity(it)){
                errors.add(
                    FieldError(
                        "maxCapacityKg", " Max Capacity Kg  must not be negative."
                    )
                )
            }
        }

        input.costPerKm?.let {
            if(!Vehicle.isValidCostPerKm(it)){
                errors.add(
                    FieldError(
                        "costPerKm", " Cost Per Km  must not be negative."
                    )
                )
            }
        }

        return if (errors.isEmpty()){
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}
    private fun hasNoFieldsToUpdate(input: UpdateVehicleInput): Boolean =
        listOfNotNull(
            input.costPerKm,
            input.maxCapacityKg,
            input.currentHub,
        ).isEmpty()

