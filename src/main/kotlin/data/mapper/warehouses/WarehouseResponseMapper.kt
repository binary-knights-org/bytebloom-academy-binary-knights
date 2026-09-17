package data.mapper.warehouses

import data.dataholder.WarehouseRaw
import data.remote.dto.warehouseDto.WarehouseResponseDto

fun WarehouseResponseDto.toRaw(): WarehouseRaw =
    WarehouseRaw(
        hubId = hubId,
        hubName = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
