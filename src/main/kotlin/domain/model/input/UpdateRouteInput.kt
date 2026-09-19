package domain.model.input

import domain.model.Warehouse
import java.util.UUID

private const val ROUTE_ID_PREFIX = "RT-"


data class UpdateRouteInput(
    val id: String= "$ROUTE_ID_PREFIX${UUID.randomUUID()}",
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null,
    val originHub: Warehouse? = null,
    val destinationHub: Warehouse? = null
)
