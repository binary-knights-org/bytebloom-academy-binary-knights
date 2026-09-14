package data.mapper

import data.dataholder.PackageRaw
import data.remote.dto.PackageDto

fun PackageDto.toRaw(): PackageRaw {
    return PackageRaw(
        packageId = id,
        weight = weight,
        originHubId = originHubId.orEmpty(),
        destinationHubId = destinationHubId.orEmpty(),
        priority = priority
    )
}


