package domain.model

import domain.exception.EntityValidationException
import domain.validator.FieldViolation
import domain.validator.IdValidator
import java.util.UUID

private const val PACKAGE_ID_PREFIX = "PKG-"
private const val MIN_WEIGHT = 0.0

data class Package(
    val id: String = "$PACKAGE_ID_PREFIX${UUID.randomUUID()}",
    val weight: Double,
    val priority: String,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    init {
        val violations = validatePackage(id, weight, priority)
        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    companion object {
        fun validatePackage(
            id: String,
            weight: Double,
            priority: String
        ): List<FieldViolation> {
            val violations = mutableListOf<FieldViolation>()
            violations.addAll(IdValidator.validate(id, PACKAGE_ID_PREFIX, "Package"))
            if (weight <= MIN_WEIGHT) {
                violations.add(FieldViolation("weight", "Weight must be greater than $MIN_WEIGHT."))
            }
            if (priority.isBlank()) {
                violations.add(FieldViolation("priority", "Priority cannot be blank."))
            }
            return violations
        }

        fun create(
            id: String = "$PACKAGE_ID_PREFIX${UUID.randomUUID()}",
            weight: Double,
            priority: String,
            originHub: Warehouse,
            destinationHub: Warehouse
        ): Result<Package> = runCatching {
            Package(id, weight, priority, originHub, destinationHub)
        }
    }
}
