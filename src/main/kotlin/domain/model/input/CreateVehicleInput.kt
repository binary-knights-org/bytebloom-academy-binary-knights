package domain.model.input

import domain.model.Warehouse
import java.util.UUID

private const val VEHICLE_ID_PREFIX = "TRK-"

data class CreateVehicleInput(
    val id: String = "$VEHICLE_ID_PREFIX${UUID.randomUUID()}",
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
)
