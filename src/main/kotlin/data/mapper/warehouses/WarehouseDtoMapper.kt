package data.mapper.warehouses

import data.dataholder.WarehouseRaw
import data.remote.dto.WarehouseResponseDto

fun WarehouseResponseDto.toRaw(): WarehouseRaw {
    return WarehouseRaw(
        hubId = hubId,
        hubName = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

