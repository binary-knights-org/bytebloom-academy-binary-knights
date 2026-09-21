package domain.model

import domain.model.exception.InvalidRouteDelayException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.SameOriginAndDestinationException
import kotlin.uuid.Uuid

data class Route(
    val id: String = "$ROUTE_ID_PREFIX${Uuid.random()}",
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {
    init {
        validateDistance()
        validateDelay()
        validateHubs()
    }

    private fun validateDistance() {
        if (distanceKm <= 0.0) {
            throw InvalidRouteDistanceException()
        }
    }

    private fun validateDelay() {
        if (typicalDelayMin < 0) {
            throw InvalidRouteDelayException()
        }
    }

    private fun validateHubs() {
        if (originHub.id == destinationHub.id) {
            throw SameOriginAndDestinationException()
        }
    }

    companion object{
        const val ROUTE_ID_PREFIX = "RT-"
    }
}
