package dataholder

import javax.lang.model.util.Elements

data class PackageRaw(
    val packageId: String,
    val weight: Double,
    val originHubId: String,
    val destinationHubId: String,
    val priority: String
)