package dataholders

data class Fleet(
    val vehicleId: String,
    val currentHubId: String,
    val maxCapacityKg: Double?,
    val costPerKm: Double?
)