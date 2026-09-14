package data.mapper

import data.dataholder.RouteRaw
import data.remote.dto.RouteDto
import domain.model.Route
import domain.model.Warehouse

fun RouteDto.toRaw(): RouteRaw {
    return RouteRaw(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}

fun RouteDto.toDomain(
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
