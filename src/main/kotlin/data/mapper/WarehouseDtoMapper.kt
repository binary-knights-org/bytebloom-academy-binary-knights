package data.mapper

import data.dataholder.WarehouseRaw
import data.remote.dto.WarehouseDto

fun WarehouseDto.toRaw(): WarehouseRaw {
    return WarehouseRaw(
        hubId = hubId,
        hubName = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

