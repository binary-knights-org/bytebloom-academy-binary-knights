package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageDto(
    @SerialName("package_id") val id: String,

    @SerialName("package_weight") val weight: Double,

    @SerialName("origin_hub_id") val originHubId: String?,

    @SerialName("destination_hub_id") val destinationHubId: String?,

    @SerialName("packages_priority") val priority: String
)


