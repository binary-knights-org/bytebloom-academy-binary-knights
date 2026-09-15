package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageResponseDto(
    @SerialName("package_id") val id: String,
    val weight: Double,
    @SerialName("origin_hub_id") val originHubId: String?,
  @SerialName("destination_hub_id") val destinationHubId: String?,
val priority: String
)


