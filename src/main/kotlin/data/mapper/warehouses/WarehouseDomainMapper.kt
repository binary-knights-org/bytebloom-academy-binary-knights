package data.mapper.warehouses

import data.local.dataholder.WarehouseRaw
import domain.model.Warehouse

fun WarehouseRaw.toDomain(): Warehouse =
    Warehouse(
        id = hubId,
        name = hubName,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )

fun Warehouse.toRaw(): WarehouseRaw =
    WarehouseRaw(
        hubId = id,
        hubName = name,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
