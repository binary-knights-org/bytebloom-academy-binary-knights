package domain.model.input

import domain.model.Warehouse

data class CreateRouteInput(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHub: Warehouse,
    val destinationHub: Warehouse
)
