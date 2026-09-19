package domain.model.input

data class CreateWarehouseInput(
    val id: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)
