package data.mapper.vehicles

import data.local.dataholder.VehicleRaw
import data.remote.dto.vehicleDto.VehicleRequestDto

fun VehicleRaw.toRequestDto(): VehicleRequestDto =
    VehicleRequestDto(
        vehicleId = vehicleIds.first(),
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
