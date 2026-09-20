package domain.model

import domain.exception.EntityValidationException
import domain.model.input.CreateRouteInput
import domain.validator.FieldViolation
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
        val violations = validateRoute( distanceKm, typicalDelayMin)
        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    private fun validateRoute(
         distanceKm: Double, typicalDelayMin: Int
    ): List<FieldViolation> {
        val violations = mutableListOf<FieldViolation>()

        if (distanceKm <= MIN_DISTANCE_KM) {
            violations.add(FieldViolation(CreateRouteInput::distanceKm.name, "Distance must be greater than $MIN_DISTANCE_KM."))
        }
        if (typicalDelayMin < MIN_DELAY_MIN) {
            violations.add(FieldViolation(CreateRouteInput::typicalDelayMin.name, "Typical delay must be at least $MIN_DELAY_MIN."))
        }
        if (originHub.id == destinationHub.id) {
            violations.add(FieldViolation(CreateRouteInput::destinationHub.name, "Origin and destination hubs cannot be the same."))
        }
        return violations
    }
}
