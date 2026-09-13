package data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDto (
    val hubId: String,
    val hubName: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)
