package domain.validator

sealed interface ValidationResult<out E> {

    data object Valid : ValidationResult<Nothing>

    data class Invalid<E>(
        val violations: List<E>
    ) : ValidationResult<E>
}
