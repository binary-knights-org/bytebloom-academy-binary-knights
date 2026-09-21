package domain.model.input

import domain.model.Warehouse
import kotlin.uuid.Uuid

data class CreateRouteInput(
    val id: String = "$ROUTE_ID_PREFIX${Uuid.random()}",
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    companion object {
        const val ROUTE_ID_PREFIX = "RT-"
    }
}
