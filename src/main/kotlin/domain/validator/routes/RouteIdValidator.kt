package domain.validator.routes

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val ROUTE_ID_PREFIX = "RT-"

class RouteIdValidator {
    fun validate(id: String): ValidationResult {
        val violations = IdValidator.validate(id, ROUTE_ID_PREFIX, "Route")
        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
