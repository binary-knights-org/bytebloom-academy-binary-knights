package domain.validator.warehouse

import domain.model.input.UpdateWarehouseInput
import domain.validator.ValidationResult

class UpdateWarehouseValidator {
    fun validate(input: UpdateWarehouseInput): ValidationResult<WarehouseValidationError> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(WarehouseValidationError.NoUpdateFields)
            }

            input.name?.let { if (it.isBlank()) add(WarehouseValidationError.BlankName) }
            input.regionalZone?.let { if (it.isBlank()) add(WarehouseValidationError.BlankRegionalZone) }

            input.latitude?.let { lat ->
                if (lat !in MIN_LATITUDE..MAX_LATITUDE) add(WarehouseValidationError.InvalidLatitude)
            }
            input.longitude?.let { lon ->
                if (lon !in MIN_LONGITUDE..MAX_LONGITUDE) add(WarehouseValidationError.InvalidLongitude)
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdateWarehouseInput): Boolean {
        return input.name == null &&
                input.regionalZone == null &&
                input.latitude == null &&
                input.longitude == null
    }

    companion object {
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
    }
}
