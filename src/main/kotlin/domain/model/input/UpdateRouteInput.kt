package domain.model.input

import domain.model.Warehouse
import kotlin.uuid.Uuid

data class UpdateRouteInput(
    val id: String= "$ROUTE_ID_PREFIX${Uuid.random()}",
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null,
    val originHub: Warehouse? = null,
    val destinationHub: Warehouse? = null
) {
    companion object{
        const val ROUTE_ID_PREFIX = "RT-"
    }
}
