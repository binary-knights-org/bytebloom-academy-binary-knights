package data.mapper

import data.dataholder.RouteRaw
import data.remote.dto.RouteResponseDto

fun RouteResponseDto.toRaw(): RouteRaw {
    return RouteRaw(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}


