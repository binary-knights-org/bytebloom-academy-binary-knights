package data.mapper.packages

import data.local.dataholder.PackageRaw
import data.remote.dto.packageDto.PackageRequestDto

fun PackageRaw.toRequestDto(): PackageRequestDto =
    PackageRequestDto(
        id = packageId,
        weight = weight,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        priority = priority
    )
