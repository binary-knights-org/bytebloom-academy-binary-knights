package domain.model.input

import java.util.UUID

private const val WAREHOUSE_ID_PREFIX = "WH-"

data class CreateWarehouseInput(
    val id: String = "$WAREHOUSE_ID_PREFIX${UUID.randomUUID()}",
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)
