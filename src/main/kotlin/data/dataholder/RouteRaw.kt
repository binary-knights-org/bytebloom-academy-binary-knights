package data.dataholder

data class RouteRaw(
    val routeId: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)
