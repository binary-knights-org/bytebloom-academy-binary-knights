package domain.model

enum class Priority {
    URGENT,
    STANDARD,
    LOW
}

fun String?.toPriority(): Priority {
    return Priority.entries.find { it.name.equals(this, ignoreCase = true) } ?: Priority.LOW
}
