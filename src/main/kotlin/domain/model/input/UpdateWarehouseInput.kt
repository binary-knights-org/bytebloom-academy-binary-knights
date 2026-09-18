package domain.model.input

data class UpdateWarehouseInput (
    val id: String,
    val name: String? = null,
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
