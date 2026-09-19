package data.mapper.routes

import data.local.dataholder.RouteRaw
import data.remote.dto.routeDto.RouteResponseDto

fun RouteResponseDto.toRaw(): RouteRaw =
    RouteRaw(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
