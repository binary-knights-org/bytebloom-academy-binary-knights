package data.remote.dto.packageDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageRequestDto(
    @SerialName("package_id") val id: String,
    @SerialName("weight") val weight: Double,
    @SerialName("origin_hub_id") val originHubId: String?,
    @SerialName("destination_hub_id") val destinationHubId: String?,
    @SerialName("priority") val priority: String
)
