package domain.validator.warehouse

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val WAREHOUSE_ID_PREFIX = "WH-"

class WarehouseIdValidator {
    fun validate(id: String): ValidationResult {
        val violations = IdValidator.validate(id, WAREHOUSE_ID_PREFIX, "Warehouse")
        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
