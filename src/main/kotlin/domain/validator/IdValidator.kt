package domain.validator

object IdValidator {

    fun validate(id: String, prefix: String, entityName: String): List<FieldViolation> {
        return when {
            id.isBlank() -> listOf(FieldViolation("id", "$entityName ID cannot be blank."))
            !id.startsWith(prefix) -> listOf(
                FieldViolation(
                    "id", "$entityName ID must start with '$prefix' or be a valid UUID."
                )
            )

            else -> emptyList()
        }
    }
}
