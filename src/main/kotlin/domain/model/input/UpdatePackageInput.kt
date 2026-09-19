package domain.model.input

import domain.model.Warehouse
import java.util.UUID

private const val PACKAGE_ID_PREFIX = "PKG-"

data class UpdatePackageInput(
    val id: String = "$PACKAGE_ID_PREFIX${UUID.randomUUID()}",
    val weight: Double? = null,
    val priority: String? = null,
    val originHub: Warehouse? = null,
    val destinationHub: Warehouse? = null
)
