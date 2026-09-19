package data.mapper.packages

import data.local.dataholder.PackageRaw
import data.remote.dto.packageDto.PackageResponseDto

fun PackageResponseDto.toRaw(): PackageRaw =
    PackageRaw(
        packageId = id,
        weight = weight,
        originHubId = originHubId.orEmpty(),
        destinationHubId = destinationHubId.orEmpty(),
        priority = priority
    )
