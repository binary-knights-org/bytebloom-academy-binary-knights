package domain.model

data class Route(
    val id: String,
    val distanceKm: Double?,
    val typicalDelayMin: Int?,
    val origin: Warehouse,
    val destination: Warehouse
)
