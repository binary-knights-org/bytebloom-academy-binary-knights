package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseRequestDto (
    @SerialName ("hub_id") val hubId: String,
    @SerialName ("hub_name") val hubName: String,
    @SerialName ("regional_zone") val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)
