package domain.validator.routes

import domain.model.Route
import domain.validator.FieldError
import domain.validator.ValidationResult

class RouteIdValidator {

    fun validate(id: String): ValidationResult {
        return if (Route.isValidId(id)) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(
                listOf(
                    FieldError(
                        "id",
                        "Route ID must start with '${Route.ID_PREFIX}' or be a valid UUID."
                    )
                )
            )
        }
    }
}