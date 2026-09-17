package domain.validator

sealed class ValidationResult {

    data object Valid : ValidationResult()

    data class Invalid(
        val violations: List<FieldViolation>
    ) : ValidationResult()
}

data class FieldViolation(
    val field: String,
    val message: String
)
