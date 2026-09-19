package domain.model.input

import domain.model.Warehouse

data class UpdateVehicleInput(
    val id: String,
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentHub: Warehouse? = null
)
