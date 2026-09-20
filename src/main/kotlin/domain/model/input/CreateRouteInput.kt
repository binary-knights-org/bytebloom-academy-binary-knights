package domain.model.input

import domain.model.Warehouse
import java.util.UUID

private const val ROUTE_ID_PREFIX = "RT-"

data class CreateRouteInput(
    val id: String = "$ROUTE_ID_PREFIX${UUID.randomUUID()}",
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHub: Warehouse,
    val destinationHub: Warehouse
)
