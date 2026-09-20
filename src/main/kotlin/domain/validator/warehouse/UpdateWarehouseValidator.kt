package domain.validator.warehouse

import domain.model.input.UpdateWarehouseInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

class UpdateWarehouseValidator(
    private val idValidator: WarehouseIdValidator
) {
    fun validate(input: UpdateWarehouseInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        if (hasNoUpdates(input)) {
            builder.addViolation("updateFields",
                "At least one field (name, regionalZone, latitude, longitude) " +
                        "must be provided for update.")
        }

        input.name?.let {
            builder.check(it.isNotBlank(), "name",
                "Warehouse name cannot be blank.")
        }

        input.regionalZone?.let {
            builder.check(it.isNotBlank(), "regionalZone",
                "Regional zone cannot be blank.")
        }

        input.latitude?.let { lat ->
            builder.check(lat in MIN_LATITUDE..MAX_LATITUDE, "latitude",
                "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE.")
        }

        input.longitude?.let { lon ->
            builder.check(lon in MIN_LONGITUDE..MAX_LONGITUDE, "longitude",
                "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE.")
        }

        return builder.build()
    }

    private fun hasNoUpdates(input: UpdateWarehouseInput): Boolean {
        return input.name == null &&
                input.regionalZone == null &&
                input.latitude == null &&
                input.longitude == null
    }
}
