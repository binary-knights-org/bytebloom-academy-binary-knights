package domain.validator.vehicle

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val VEHICLE_ID_PREFIX = "TRK-"

class VehicleIdValidator {
    fun validate(id: String): ValidationResult {
        val violations = IdValidator.validate(id, VEHICLE_ID_PREFIX, "Vehicle")
        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
