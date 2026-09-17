package data.mapper.packages

import data.dataholder.PackageRaw
import domain.model.Package
import domain.model.Warehouse

fun PackageRaw.toDomain(
    warehousesById: Map<String, Warehouse>
): Package? {
    val originWarehouse = warehousesById[originHubId] ?: return null
    val destinationWarehouse = warehousesById[destinationHubId] ?: return null

    return runCatching {
        Package.create(
            id = packageId,
            weight = weight,
            priority = priority,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        )
    }.getOrNull()
}

fun Package.toRaw(): PackageRaw =
    PackageRaw(
        packageId = id,
        weight = weight,
        originHubId = originHub.id,
        destinationHubId = destinationHub.id,
        priority = priority
    )
