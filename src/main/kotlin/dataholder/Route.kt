package dataholder

data class RouteRaw(
    val id: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)