package data.mapper

import data.dataholder.VehicleRaw
import domain.model.Vehicle
import domain.model.Warehouse

fun VehicleRaw.toDomain(
    warehousesById: Map<String, Warehouse>
): Vehicle? {
    val currentWarehouse = warehousesById[currentHubId]
    val vehicleId = vehicleIds.firstOrNull()

    return when {
        currentWarehouse == null || vehicleId == null -> null

        else -> Vehicle(
            id = vehicleId,
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm,
            currentHub = currentWarehouse
        )
    }
}
