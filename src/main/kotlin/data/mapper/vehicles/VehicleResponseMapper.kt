package data.mapper.vehicles

import data.dataholder.VehicleRaw
import data.remote.dto.VehicleResponseDto

fun VehicleResponseDto.toRaw(): VehicleRaw {
    return VehicleRaw(
        vehicleIds = listOf(vehicleId),
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}
