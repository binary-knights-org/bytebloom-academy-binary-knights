package domain.model.input

import kotlin.uuid.Uuid

data class UpdateWarehouseInput (
    val id: String= "$WAREHOUSE_ID_PREFIX${Uuid.random()}",
    val name: String? = null,
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    companion object {
        const val WAREHOUSE_ID_PREFIX = "WH-"
    }
}
