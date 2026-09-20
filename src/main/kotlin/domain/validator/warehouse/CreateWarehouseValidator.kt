package domain.validator.warehouse

import domain.model.input.CreateWarehouseInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

class CreateWarehouseValidator(

) {
    fun validate(input: CreateWarehouseInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.name.isBlank()){
            violations.add(
                FieldViolation(
                    CreateWarehouseInput::name.name,
                    "Warehouse name must not be blank."
                )
            )
        }

        if (input.regionalZone.isBlank()) violations.add(
            FieldViolation(
                CreateWarehouseInput::regionalZone.name,
                "Warehouse regional zone must not be blank."
            )
        )
        if (input.latitude !in MIN_LATITUDE..MAX_LATITUDE){
            violations.add(
                FieldViolation(
                    CreateWarehouseInput::latitude.name,
                    "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."
                )
            )
        }

        if (input.longitude !in MIN_LONGITUDE..MAX_LONGITUDE){
            violations.add(
                FieldViolation(
                    CreateWarehouseInput::longitude.name,
                    "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
                )
            )
        }

        return violations.toValidationResult()
    }
}
