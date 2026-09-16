package domain.validator

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<FieldError>) : ValidationResult()
}

data class FieldError(
    val faildName : String,
    val message: String
)
