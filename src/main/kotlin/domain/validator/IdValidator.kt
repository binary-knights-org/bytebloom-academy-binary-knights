package domain.validator

import domain.model.exception.BlankIdException
import domain.model.exception.DomainException
import domain.model.exception.InvalidIdFormatException

object IdValidator {

    fun validate(id: String, prefix: String, entityName: String): List<DomainException> {
        return when {
            id.isBlank() -> listOf(BlankIdException(entityName))
            !id.startsWith(prefix) -> listOf(InvalidIdFormatException(entityName, prefix))
            else -> emptyList()
        }
    }
}
