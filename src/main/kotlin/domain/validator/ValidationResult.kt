package domain.validator

import domain.model.exception.DomainException

sealed class ValidationResult<out T> {
    data class Success<out T>(val value: T) : ValidationResult<T>()
    data class Failure(val errors: List<DomainException>) : ValidationResult<Nothing>()
}

