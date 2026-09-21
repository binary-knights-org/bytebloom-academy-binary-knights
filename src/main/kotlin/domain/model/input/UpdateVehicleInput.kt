package domain.model.input

import domain.model.Warehouse
import kotlin.uuid.Uuid

data class UpdateVehicleInput(
    val id: String = "$VEHICLE_ID_PREFIX${Uuid.random()}",
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentHub: Warehouse? = null
) {
    companion object {
        const val VEHICLE_ID_PREFIX = "TRK-"
    }
}
