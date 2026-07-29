package domain.model

data class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
)
