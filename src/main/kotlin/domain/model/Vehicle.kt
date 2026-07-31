package domain.model

data class Vehicle(
    val id: List<String>,
    val maxCapacityKg: Double?,
    val costPerKm: Double?,
    val currentHub: Warehouse
)
