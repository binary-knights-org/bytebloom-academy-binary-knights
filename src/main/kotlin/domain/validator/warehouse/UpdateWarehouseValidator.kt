package domain.validator.warehouse

import domain.model.input.UpdateWarehouseInput
import domain.exception.BlankFieldException
import domain.exception.DomainValidationException
import domain.exception.InvalidCoordinateException
import domain.exception.NoUpdateFieldsException
import domain.validator.ValidationResult

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

class UpdateWarehouseValidator(
    private val idValidator: WarehouseIdValidator
) {
    fun validate(input: UpdateWarehouseInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainValidationException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (hasNoUpdates(input)) {
            errors += NoUpdateFieldsException("name, regionalZone, latitude, longitude")
        }

        input.name?.let {
            if (it.isBlank()) errors += BlankFieldException("Warehouse name")
        }

        input.regionalZone?.let {
            if (it.isBlank()) errors += BlankFieldException("Regional zone")
        }

        input.latitude?.let { lat ->
            if (lat !in MIN_LATITUDE..MAX_LATITUDE) {
                errors += InvalidCoordinateException("Latitude", MIN_LATITUDE, MAX_LATITUDE)
            }
        }

        input.longitude?.let { lon ->
            if (lon !in MIN_LONGITUDE..MAX_LONGITUDE) {
                errors += InvalidCoordinateException("Longitude", MIN_LONGITUDE, MAX_LONGITUDE)
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }

    private fun hasNoUpdates(input: UpdateWarehouseInput): Boolean {
        return input.name == null &&
                input.regionalZone == null &&
                input.latitude == null &&
                input.longitude == null
    }
}
