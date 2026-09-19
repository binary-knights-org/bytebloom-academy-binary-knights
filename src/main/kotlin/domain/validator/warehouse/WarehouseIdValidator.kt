package domain.validator.warehouse

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val WAREHOUSE_ID_PREFIX = "WH-"

class WarehouseIdValidator {
    fun validate(id: String): ValidationResult<Unit> {
        val errors = IdValidator.validate(id, WAREHOUSE_ID_PREFIX, "Warehouse")
        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
