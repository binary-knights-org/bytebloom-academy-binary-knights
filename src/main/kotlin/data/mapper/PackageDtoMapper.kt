package data.mapper

import data.dataholder.PackageRaw
import data.remote.dto.PackageResponseDto

fun PackageResponseDto.toRaw(): PackageRaw {
    return PackageRaw(
        packageId = id,
        weight = weight,
        originHubId = originHubId.orEmpty(),
        destinationHubId = destinationHubId.orEmpty(),
        priority = priority
    )
}


