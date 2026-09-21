package data.remote.dto.warehouseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseRequestDto (
    @SerialName ("hub_id") val hubId: String,
    @SerialName ("hub_name") val hubName: String,
    @SerialName ("regional_zone") val regionalZone: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double
)
