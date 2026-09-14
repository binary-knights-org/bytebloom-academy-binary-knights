package data.mapper

import data.dataholder.WarehouseRaw
import data.remote.dto.WarehouseDto
import domain.model.Warehouse

fun WarehouseRaw.toDomain(): Warehouse {
    return Warehouse(
        id = hubId,
        name = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

fun WarehouseDto.toRaw(): WarehouseRaw {
    return WarehouseRaw(
        hubId = hubId,
        hubName = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

