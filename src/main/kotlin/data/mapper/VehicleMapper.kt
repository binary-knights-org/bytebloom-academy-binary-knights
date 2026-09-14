package data.mapper

import data.dataholder.VehicleRaw
import data.remote.dto.VehicleRequestDto
import data.remote.dto.VehicleResponseDto
import domain.model.Vehicle
import domain.model.Warehouse


fun VehicleResponseDto.toRaw(): VehicleRaw {
    return VehicleRaw(
        vehicleIds = listOf(vehicleId),
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}
fun VehicleRaw.toRequestDto(): VehicleRequestDto {
    return VehicleRequestDto(
        vehicleId = vehicleIds.first(),
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}
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


