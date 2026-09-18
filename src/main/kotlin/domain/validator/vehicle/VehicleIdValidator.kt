package domain.validator.vehicle

import domain.model.Vehicle
import domain.validator.FieldError
import domain.validator.ValidationResult

class VehicleIdValidator {

    fun validate(id: String): ValidationResult {
        return if (Vehicle.isValidId(id)) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(
                listOf(
                    FieldError(
                        "id",
                        "Vehicle ID must start with '${Vehicle.ID_PREFIX}' or be a valid UUID."
                    )
                )
            )
        }
    }
}
