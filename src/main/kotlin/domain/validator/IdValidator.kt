package domain.validator

import domain.exception.BlankIdException
import domain.exception.DomainValidationException
import domain.exception.InvalidIdFormatException

object IdValidator {

    fun validate(id: String, prefix: String, entityName: String): List<DomainValidationException> {
        return when {
            id.isBlank() -> listOf(BlankIdException(entityName))
            !id.startsWith(prefix) -> listOf(InvalidIdFormatException(entityName, prefix))
            else -> emptyList()
        }
    }
}
