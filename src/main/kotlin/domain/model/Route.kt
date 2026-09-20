package domain.model

import domain.exception.EntityValidationException
import domain.validator.FieldViolation
import domain.validator.IdValidator
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
        val violations = validateRoute(id, distanceKm, typicalDelayMin)
        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    private fun validateRoute(
        id: String, distanceKm: Double, typicalDelayMin: Int
    ): List<FieldViolation> {
        val violations = mutableListOf<FieldViolation>()
        violations.addAll(IdValidator.validate(id, ROUTE_ID_PREFIX, "Route"))
        if (distanceKm <= MIN_DISTANCE_KM) {
            violations.add(FieldViolation("distanceKm", "Distance must be greater than $MIN_DISTANCE_KM."))
        }
        if (typicalDelayMin < MIN_DELAY_MIN) {
            violations.add(FieldViolation("typicalDelayMin", "Typical delay must be at least $MIN_DELAY_MIN."))
        }
        return violations
    }
}
