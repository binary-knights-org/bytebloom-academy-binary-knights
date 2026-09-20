package domain.model

import domain.exception.EntityValidationException
import domain.model.input.CreatePackageInput
import domain.validator.FieldViolation

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
        val violations = validatePackage( weight, priority)
        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    private fun validatePackage(
         weight: Double, priority: String
    ): List<FieldViolation> {
        val violations = mutableListOf<FieldViolation>()

        if (weight <= MIN_WEIGHT) {
            violations.add(FieldViolation(CreatePackageInput::weight.name, message = "Weight must be greater than $MIN_WEIGHT."))
        }
        if (priority.isBlank()) {
            violations.add(FieldViolation(CreatePackageInput::priority.name, message = "Priority cannot be blank."))
        }

        return violations
    }
}
