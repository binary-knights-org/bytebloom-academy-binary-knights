package domain.model.input

import domain.model.Warehouse
import kotlin.uuid.Uuid

data class CreateVehicleInput(
    val id: String = "$VEHICLE_ID_PREFIX${Uuid.random()}",
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
) {
    companion object {
        const val VEHICLE_ID_PREFIX = "TRK-"
    }
}
