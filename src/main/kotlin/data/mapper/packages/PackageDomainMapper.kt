package data.mapper.packages

import data.local.dataholder.PackageRaw
import domain.model.Package
import domain.model.Priority
import domain.model.Warehouse
import domain.model.toPriority

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
            priority = priority.toPriority(),
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        )
    }
}

fun Package.toRaw(): PackageRaw = PackageRaw(
    packageId = id,
    weight = weight,
    originHubId = originHub.id,
    destinationHubId = destinationHub.id,
    priority = priority.name
)
