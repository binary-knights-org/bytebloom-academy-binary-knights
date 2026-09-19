package domain.model

import domain.validator.IdValidator
import domain.exception.InvalidDelayException
import domain.exception.InvalidDistanceException
import java.util.UUID

private const val ROUTE_ID_PREFIX = "RT-"
private const val MIN_DISTANCE_KM = 0.0
private const val MIN_DELAY_MIN = 0

data class Route(
    val id: String = "$ROUTE_ID_PREFIX${UUID.randomUUID()}",
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    init {
        validateId()
        validateDistance()
        validateDelay()
    }

    private fun validateId() {
        val errors = IdValidator.validate(id, ROUTE_ID_PREFIX, "Route")
        if (errors.isNotEmpty()) throw errors.first()
    }

    private fun validateDistance() {
        if (distanceKm <= MIN_DISTANCE_KM) throw InvalidDistanceException(MIN_DISTANCE_KM)
    }

    private fun validateDelay() {
        if (typicalDelayMin < MIN_DELAY_MIN) throw InvalidDelayException(MIN_DELAY_MIN)
    }
}
