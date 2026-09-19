package domain.validator.vehicle

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val VEHICLE_ID_PREFIX = "TRK-"

class VehicleIdValidator {
    fun validate(id: String): ValidationResult<Unit> {
        val errors = IdValidator.validate(id, VEHICLE_ID_PREFIX, "Vehicle")
        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
