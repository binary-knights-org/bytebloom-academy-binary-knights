package domain.model

import domain.validator.IdValidator
import domain.exception.InvalidWeightException
import java.util.UUID

private const val PACKAGE_ID_PREFIX = "PKG-"
private const val MIN_WEIGHT = 0.0

data class Package(
    val id: String= "$PACKAGE_ID_PREFIX${UUID.randomUUID()}",
    val weight: Double,
    val priority: String,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    init {
        validateId()
        validateWeight()
    }

    private fun validateId() {
        val errors = IdValidator.validate(id, PACKAGE_ID_PREFIX, "Package")
        if (errors.isNotEmpty()) throw errors.first()
    }

    private fun validateWeight() {
        if (weight <= MIN_WEIGHT) throw InvalidWeightException(MIN_WEIGHT)
    }
}
