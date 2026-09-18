package domain.validator.warehouse

import domain.model.Warehouse
import domain.model.input.UpdateWarehouseInput
import domain.validator.FieldError
import domain.validator.ValidationResult

class UpdateWarehouseValidator {
    fun validate(
        input: UpdateWarehouseInput
    ): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (input.regionalZone == null && input.latitude == null  &&  input.longitude == null &&  input.name == null) {
            errors.add(
                FieldError(
                    "update", "At least one field must be provided for update."
                )
            )
        }

        input.name?.let {
            if (!Warehouse.isValidName(it)){
                errors.add(
                    FieldError(
                        "hub name", "Warehouse name must not be blank"
                    )
                )
            }
        }

        input.regionalZone?.let {
            if(!Warehouse.isValidRegionalZone(it)){
                errors.add(
                    FieldError(
                        "regionalZone", "Regional zone must not be blank"
                    )
                )
            }
        }


        input.latitude?.let {
            if (!Warehouse.isValidLatitude(it)){
                errors.add(
                    FieldError(
                        "Latitude", "Latitude must be between -90 and 90"
                    )
                )
            }
        }

        input.longitude?.let {
            if(!Warehouse.isValidlongitude(it)){
                errors.add(
                    FieldError(
                        "Longitude", "Longitude must be between -180 and 180"
                    )
                )
            }
        }
        return if (errors.isEmpty()){
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}
