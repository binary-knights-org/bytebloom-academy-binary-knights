package data.mapper

import data.dataholder.WarehouseRaw
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
