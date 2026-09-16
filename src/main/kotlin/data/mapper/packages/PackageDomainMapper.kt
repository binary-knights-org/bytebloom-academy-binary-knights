package data.mapper.packages

import data.dataholder.PackageRaw
import domain.model.Package
import domain.model.Warehouse

fun PackageRaw.toDomain(
    warehousesById: Map<String, Warehouse>
): Package? {
    val originWarehouse = warehousesById[originHubId]
    val destinationWarehouse = warehousesById[destinationHubId]

    return when {
        originWarehouse == null || destinationWarehouse == null -> null

        else -> Package(
            id = packageId,
            weight = weight,
            priority = priority,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        )
    }
}

fun Package.toRaw(): PackageRaw =
    PackageRaw(
        packageId = id,
        weight = weight,
        originHubId = originHub.id,
        destinationHubId = destinationHub.id,
        priority = priority
    )
