package domain.model.input

import domain.model.Warehouse

data class CreateVehicleInput(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
)
