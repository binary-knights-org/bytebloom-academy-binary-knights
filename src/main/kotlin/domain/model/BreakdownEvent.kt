package domain.model

data class BreakdownEvent(
    val slot: Int,
    val brokenVehicle: Vehicle,
    val fallbackVehicle: Vehicle
)
