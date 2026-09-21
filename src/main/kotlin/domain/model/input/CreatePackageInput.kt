package domain.model.input

import domain.model.Warehouse
import kotlin.uuid.Uuid

data class CreatePackageInput(
    val id: String = "$PACKAGE_ID_PREFIX${Uuid.random()}",
    val weight: Double,
    val priority: String,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    companion object {
        const val PACKAGE_ID_PREFIX = "PKG-"
    }
}
