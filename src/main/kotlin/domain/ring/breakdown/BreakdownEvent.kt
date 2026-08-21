package domain.ring.breakdown

import domain.model.Vehicle

data class BreakdownEvent(
    val slot: Int,
    val brokenVehicle: Vehicle,
    val fallbackVehicle: Vehicle
)
