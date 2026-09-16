package data.mapper.routes

import data.dataholder.RouteRaw
import data.remote.dto.routeDto.RouteResponseDto

fun RouteRaw.toRequestDto(): RouteResponseDto =
    RouteResponseDto(
        routeId =  routeId,
        originHubId =  originHubId,
        destinationHubId =  destinationHubId,
        distanceKm =  distanceKm,
        typicalDelayMin =  typicalDelayMin
    )
