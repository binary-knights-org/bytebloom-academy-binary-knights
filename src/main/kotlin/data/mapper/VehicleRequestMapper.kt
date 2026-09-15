package data.mapper

import data.dataholder.VehicleRaw
import data.remote.dto.VehicleRequestDto

fun VehicleRaw.toRequestDto(): VehicleRequestDto {
    return VehicleRequestDto(
        vehicleId = vehicleIds.first(),
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}
