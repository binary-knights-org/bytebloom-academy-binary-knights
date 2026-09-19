package domain.model.input

import domain.model.Warehouse
import java.util.UUID

private const val PACKAGE_ID_PREFIX = "PKG-"

data class CreatePackageInput(
    val id: String = "$PACKAGE_ID_PREFIX${UUID.randomUUID()}",
    val weight: Double,
    val priority: String,
    val originHub: Warehouse,
    val destinationHub: Warehouse
)
