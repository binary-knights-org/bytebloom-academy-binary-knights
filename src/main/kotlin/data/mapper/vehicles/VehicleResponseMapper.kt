package data.mapper.vehicles

import data.local.dataholder.VehicleRaw
import data.remote.dto.vehicleDto.VehicleResponseDto

fun VehicleResponseDto.toRaw(): VehicleRaw =
    VehicleRaw(
        vehicleIds = listOf(vehicleId),
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
