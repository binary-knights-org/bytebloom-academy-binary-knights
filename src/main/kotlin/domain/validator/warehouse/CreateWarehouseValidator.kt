package domain.validator.warehouse

import domain.model.input.CreateWarehouseInput
import domain.validator.ValidationResultBuilder
import domain.validator.ValidationResult

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

class CreateWarehouseValidator(
    private val idValidator: WarehouseIdValidator
) {
    fun validate(input: CreateWarehouseInput): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Invalid) {
            builder.addViolations(idResult.violations)
        }

        builder.check(
            input.name.isNotBlank(),
            "name",
            "Warehouse name cannot be blank."
        )
        builder.check(
            input.regionalZone.isNotBlank(),
            "regionalZone",
            "Regional zone cannot be blank."
        )
        builder.check(
            input.latitude in MIN_LATITUDE..MAX_LATITUDE,
            "latitude",
            "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."
        )
        builder.check(
            input.longitude in MIN_LONGITUDE..MAX_LONGITUDE,
            "longitude",
            "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
        )

        return builder.build()
    }
}
