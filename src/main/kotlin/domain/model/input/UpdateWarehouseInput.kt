package domain.model.input

import java.util.UUID

private const val WAREHOUSE_ID_PREFIX = "WH-"

data class UpdateWarehouseInput (
    val id: String= "$WAREHOUSE_ID_PREFIX${UUID.randomUUID()}",
    val name: String? = null,
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
