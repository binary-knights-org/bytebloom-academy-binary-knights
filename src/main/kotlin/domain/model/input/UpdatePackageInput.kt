package domain.model.input

import domain.model.Warehouse
import kotlin.uuid.Uuid

data class UpdatePackageInput(
    val id: String = "$PACKAGE_ID_PREFIX${Uuid.random()}",
    val weight: Double? = null,
    val priority: String? = null,
    val originHub: Warehouse? = null,
    val destinationHub: Warehouse? = null
) {
    companion object {
        const val PACKAGE_ID_PREFIX = "PKG-"
    }
}
