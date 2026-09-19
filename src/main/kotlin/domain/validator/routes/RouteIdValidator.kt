package domain.validator.routes

import domain.validator.IdValidator
import domain.validator.ValidationResult

private const val ROUTE_ID_PREFIX = "RT-"

class RouteIdValidator {

    fun validate(id: String): ValidationResult<Unit> {
        val errors = IdValidator.validate(id, ROUTE_ID_PREFIX, "Route")
        return if (errors.isEmpty()) ValidationResult.Success(Unit) else ValidationResult.Failure(errors)
    }
}
