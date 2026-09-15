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


