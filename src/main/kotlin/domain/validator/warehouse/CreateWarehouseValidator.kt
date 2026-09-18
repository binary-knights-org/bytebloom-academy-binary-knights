package domain.validator.warehouse

import domain.model.Warehouse
import domain.validator.FieldError
import domain.validator.ValidationResult

class CreateWarehouseValidator (
    private val idValidator: WarehouseIdValidator
) {
    fun validate(warehouse: Warehouse): ValidationResult {
        val errors = mutableListOf<FieldError>()

        val idResult = idValidator.validate(warehouse.id)

        if (idResult is ValidationResult.Failure) {

            errors.addAll(idResult.errors)
        }

        if (warehouse.name.isBlank()) {
            errors.add(
                FieldError(
                    "hub name",
                    "Hub name must not be blank."
                )
            )
        }

        if (warehouse.regionalZone.isBlank()){
            errors.add(
                FieldError(
                    "Regional zone",
                    " Regional zone must not be blank "
                )
            )
        }

        if (!Warehouse.isValidlongitude(warehouse.longitude)){
            errors.add(
                FieldError(
                    "longitude",
                    "Longitude must be between -180 and 180"
                )
            )
        }

        if (!Warehouse.isValidLatitude(warehouse.latitude)){
            errors.add(
                FieldError(
                    "latitude",
                    "Latitude must be between -90 and 90 "
                )
            )
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else  {
            ValidationResult.Failure(errors)
        }
    }
}
