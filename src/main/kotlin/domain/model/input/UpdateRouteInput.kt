package domain.model.input

import domain.model.Warehouse

data class UpdateRouteInput(
    val id: String,
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null,
    val originHub: Warehouse? = null,
    val destinationHub: Warehouse? = null
)
