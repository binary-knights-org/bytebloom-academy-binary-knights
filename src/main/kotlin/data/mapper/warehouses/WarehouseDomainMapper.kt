package data.mapper.warehouses

import data.local.dataholder.WarehouseRaw
import domain.model.Warehouse
import domain.model.RegionalZone

fun WarehouseRaw.toDomain(): Warehouse =
    Warehouse(
        id = hubId,
        name = hubName,
        regionalZone = RegionalZone.valueOf(regionalZone.trim().uppercase()),
        latitude = latitude,
        longitude = longitude
    )

fun Warehouse.toRaw(): WarehouseRaw =
    WarehouseRaw(
        hubId = id,
        hubName = name,
        regionalZone = regionalZone.name,
        latitude = latitude,
        longitude = longitude
    )
