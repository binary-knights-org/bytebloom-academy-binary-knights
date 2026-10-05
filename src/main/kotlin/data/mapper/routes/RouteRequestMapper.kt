package data.mapper.routes

import data.local.dataholder.RouteRaw
import data.remote.dto.routeDto.RouteRequestDto

fun RouteRaw.toRequestDto(): RouteRequestDto =
    RouteRequestDto(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
