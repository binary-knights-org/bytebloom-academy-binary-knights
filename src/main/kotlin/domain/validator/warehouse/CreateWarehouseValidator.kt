package domain.validator.warehouse

import domain.model.input.CreateWarehouseInput
import domain.model.exception.BlankFieldException
import domain.model.exception.DomainException
import domain.model.exception.InvalidCoordinateException
import domain.validator.ValidationResult

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

class CreateWarehouseValidator(
    private val idValidator: WarehouseIdValidator
) {
    fun validate(input: CreateWarehouseInput): ValidationResult<Unit> {
        val errors = mutableListOf<DomainException>()

        val idResult = idValidator.validate(input.id)
        if (idResult is ValidationResult.Failure) {
            errors += idResult.errors
        }

        if (input.name.isBlank()) errors += BlankFieldException("Warehouse name")
        if (input.regionalZone.isBlank()) errors += BlankFieldException("Regional zone")

        if (input.latitude !in MIN_LATITUDE..MAX_LATITUDE) {
            errors += InvalidCoordinateException("Latitude", MIN_LATITUDE, MAX_LATITUDE)
        }
        if (input.longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
            errors += InvalidCoordinateException("Longitude", MIN_LONGITUDE, MAX_LONGITUDE)
        }

        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
