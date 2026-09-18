package domain.validator.vehicle

import domain.model.Vehicle
import domain.validator.ValidationResult
import domain.validator.FieldError


class CreateVehicleValidator(
    private val idValidator: VehicleIdValidator
) {

    fun validate(vehicle: Vehicle): ValidationResult {
        val errors = mutableListOf<FieldError>()

        val idResult = idValidator.validate(vehicle.id)

        if (idResult is ValidationResult.Failure) {
            errors.addAll(idResult.errors)
        }

        if (!Vehicle.isValidCapacity(vehicle.maxCapacityKg)){
            errors.add(
                FieldError(
                    "Max Capacity",
                    "Capacity of ${vehicle.maxCapacityKg} is invalid"
                )
            )
        }
        if (!Vehicle.isValidCostPerKm(vehicle.costPerKm)){
            errors.add(
                FieldError(
                    "Cost Per Km",
                    " cost per km of ${vehicle.costPerKm} is invalid "
                )
            )
        }

        if (vehicle.currentHub.id.isBlank()) {
            errors.add(
                FieldError(
                    "currentHubId",
                    "Hub ID must not be blank."
                )
            )
        }


        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else  {
            ValidationResult.Failure(errors)
        }
    }
}
