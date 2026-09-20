package domain.validator.warehouse

import domain.model.input.UpdateWarehouseInput
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.toValidationResult

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

class UpdateWarehouseValidator {

    fun validate(input: UpdateWarehouseInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(
                FieldViolation(
                    field = UpdateWarehouseInput::id.name,
                    message = "Warehouse ID must not be blank."
                )
            )
        }

        input.name?.let { name ->
            if (name.isBlank()) {
                violations.add(
                    FieldViolation(
                        field = UpdateWarehouseInput::name.name,
                        message = "Warehouse name must not be blank."
                    )
                )
            }
        }

        input.regionalZone?.let { zone ->
            if (zone.isBlank()) {
                violations.add(
                    FieldViolation(
                        field = UpdateWarehouseInput::regionalZone.name,
                        message = "Warehouse regional zone must not be blank."
                    )
                )
            }
        }

        input.latitude?.let { lat ->
            if (lat !in MIN_LATITUDE..MAX_LATITUDE) {
                violations.add(
                    FieldViolation(
                        field = UpdateWarehouseInput::latitude.name,
                        message = "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."
                    )
                )
            }
        }

        input.longitude?.let { lon ->
            if (lon !in MIN_LONGITUDE..MAX_LONGITUDE) {
                violations.add(
                    FieldViolation(
                        field = UpdateWarehouseInput::longitude.name,
                        message = "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
                    )
                )
            }
        }

        return violations.toValidationResult()
    }
}
