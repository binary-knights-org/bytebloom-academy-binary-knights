package data.mapper.routes

import data.dataholder.RouteRaw
import domain.model.Route
import domain.model.Warehouse

fun RouteRaw.toDomain(
    warehousesById: Map<String, Warehouse>
): Route? {
    val originWarehouse = warehousesById[originHubId]
    val destinationWarehouse = warehousesById[destinationHubId]

    return when {
        originWarehouse == null || destinationWarehouse == null -> null
        else -> Route(
            id = routeId,
            distanceKm = distanceKm,
            typicalDelayMin = typicalDelayMin,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        )
    }
}

fun Route.toRaw(): RouteRaw =
    RouteRaw(
        routeId = id,
        originHubId = originHub.id,
        destinationHubId = destinationHub.id,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
