package domain.validator.warehouse

import domain.model.Warehouse
import domain.validator.FieldError
import domain.validator.ValidationResult

class WarehouseIdValidator {
    fun validate(id: String): ValidationResult {
        return if (Warehouse.isValidId(id)) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(
                listOf(
                    FieldError(
                        "id",
                        "Warehouse ID must start with '${Warehouse.ID_PREFIX}' or be a valid UUID."
                    )
                )
            )
        }
    }
}
