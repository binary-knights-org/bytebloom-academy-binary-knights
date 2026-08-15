package data.dataholder

data class VehicleRaw(
    val vehicleIds: List<String>,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)
