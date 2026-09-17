package domain.exception

import domain.validator.warehouse.FieldViolation


sealed class DomainException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)


class EntityValidationException(
    val violations: List<FieldViolation>,
    message: String = "Validation failed: ${violations.joinToString("; ") { "${it.field}: ${it.message}" }}"
) : DomainException(message) {
    constructor(field: String, message: String) : this(listOf(FieldViolation(field, message)))
}

class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)

class DatabaseConflictException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)

class NetworkUnavailableException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)
