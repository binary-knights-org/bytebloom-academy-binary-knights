package data.mapper.warehouses

import data.local.dataholder.WarehouseRaw
import data.remote.dto.warehouseDto.WarehouseRequestDto

fun WarehouseRaw.toRequestDto(): WarehouseRequestDto =
    WarehouseRequestDto(
        hubId = hubId,
        hubName = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
