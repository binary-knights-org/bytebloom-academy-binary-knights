package domain.repository

interface RouteRepository {
    data class RouteRecord(
        val routeId: String,
        val originHubId: String,
        val destinationHubId: String,
        val distanceKm: Double,
        val typicalDelayMin: Int
    )

    fun getAllRoutes(): List<RouteRecord>
}
