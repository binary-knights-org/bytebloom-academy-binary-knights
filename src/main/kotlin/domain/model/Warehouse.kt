package domain.model

data class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String,
    val cargoQueue: List<Package> = emptyList(),
    val outgoingRoutes: List<Route> = emptyList(),
    val stationedVehicles: List<Vehicle> = emptyList()
)
