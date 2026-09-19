package domain.validator

import domain.exception.DomainValidationException

sealed class ValidationResult<out T> {
    data class Success<out T>(val value: T) : ValidationResult<T>()
    data class Failure(val errors: List<DomainValidationException>) : ValidationResult<Nothing>()
}

