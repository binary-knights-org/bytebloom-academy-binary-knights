package domain.validator

data class FieldViolation(
    val field: String,
    val message: String
)

sealed interface ValidationResult {
    data object Valid : ValidationResult

    data class Invalid(val violations: List<FieldViolation>) : ValidationResult {
        constructor(field: String, message: String) : this(listOf(FieldViolation(field, message)))
    }

    val isValid: Boolean get() = this is Valid
    val isInvalid: Boolean get() = this is Invalid

    fun errorsOrNull(): List<FieldViolation>? = (this as? Invalid)?.violations
}

fun List<FieldViolation>.toValidationResult(): ValidationResult =
    if (isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(this)
