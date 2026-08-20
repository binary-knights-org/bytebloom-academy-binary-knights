package domain.repository

interface WarehouseRepository {
    data class WarehouseRecord(
        val hubId: String,
        val hubName: String,
        val regionalZone: String,
        val latitude: Double,
        val longitude: Double
    )

    fun getAllWarehouses(): List<WarehouseRecord>
}
