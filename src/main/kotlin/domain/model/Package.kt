package domain.model

import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.SameOriginAndDestinationException
import kotlin.uuid.Uuid

data class Package(
    val id: String = "$PACKAGE_ID_PREFIX${Uuid.random()}",
    val weight: Double,
    val priority: Priority,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    init {
        validateWeight()
        validateHubs()
    }

    private fun validateWeight() {
        if (weight <= MIN_WEIGHT) {
            throw InvalidPackageWeightException()
        }
    }

    private fun validateHubs() {
        if (originHub.id == destinationHub.id) {
            throw SameOriginAndDestinationException("Origin and destination hubs cannot be the same")
        }
    }

    companion object {
        const val PACKAGE_ID_PREFIX = "PKG-"
        const val MIN_WEIGHT = 0.0
    }
}
