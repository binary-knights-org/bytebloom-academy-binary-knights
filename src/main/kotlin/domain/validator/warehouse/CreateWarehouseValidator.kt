package domain.validator.warehouse

import domain.model.input.CreateWarehouseInput
import domain.validator.ValidationResult

class CreateWarehouseValidator {
    fun validate(input: CreateWarehouseInput): ValidationResult<WarehouseValidationError> {
        val violations = buildList {
            if (input.name.isBlank()) add(WarehouseValidationError.BlankName)
            if (input.regionalZone.isBlank()) add(WarehouseValidationError.BlankRegionalZone)
            if (input.latitude !in MIN_LATITUDE..MAX_LATITUDE) {
                add(WarehouseValidationError.InvalidLatitude)
            }
            if (input.longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
                add(WarehouseValidationError.InvalidLongitude)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    companion object {
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
    }
}
