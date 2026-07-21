package dataholders

data class Vehicle(
    val vehicleId: String,
    val currentHubId: String,
    val maxCapacityKg: Double?,
    val costPerKm: Double?
)