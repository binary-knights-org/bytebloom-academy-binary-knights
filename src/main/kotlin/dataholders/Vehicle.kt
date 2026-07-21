package dataholders

data class Vehicle(
    val id: String,
    val currentHubId: String,
    val maxCapacityKg: Double?,
    val costPerKm: Double?
)