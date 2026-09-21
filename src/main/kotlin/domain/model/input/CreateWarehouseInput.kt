package domain.model.input

import kotlin.uuid.Uuid

data class CreateWarehouseInput(
    val id: String = "$WAREHOUSE_ID_PREFIX${Uuid.random()}",
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        const val WAREHOUSE_ID_PREFIX = "WH-"
    }
}
