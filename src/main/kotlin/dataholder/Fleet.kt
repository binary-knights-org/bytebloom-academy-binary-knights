package dataholder

data class FleetRaw(
    val ids: List<String>,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)