package domain.model

import domain.exception.EntityValidationException
import java.util.UUID

data class Route private constructor(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHub: Warehouse,
    val destinationHub: Warehouse
) {

    companion object {

        const val ID_PREFIX = "RT-"
        const val MIN_DISTANCE_KM = 0.0
        const val MIN_DELAY_MIN = 0

        fun isValidId(id: String): Boolean {
            if (id.isBlank()) return false

            val hasValidPrefix = id.startsWith(ID_PREFIX)

            val isUuid = runCatching {
                UUID.fromString(id)
            }.isSuccess

            return hasValidPrefix || isUuid
        }

        fun isValidDistance(distanceKm: Double): Boolean {
            return distanceKm > MIN_DISTANCE_KM
        }

        fun isValidDelay(typicalDelayMin: Int): Boolean {
            return typicalDelayMin >= MIN_DELAY_MIN
        }

        fun create(
            id: String,
            distanceKm: Double,
            typicalDelayMin: Int,
            originHub: Warehouse,
            destinationHub: Warehouse
        ): Route {

            if (!isValidId(id)) {
                throw EntityValidationException(
                    "Invalid Route ID format (Must start with RT- or be a valid UUID)."
                )
            }

            if (!isValidDistance(distanceKm)) {
                throw EntityValidationException(
                    "Distance must be greater than $MIN_DISTANCE_KM."
                )
            }

            if (!isValidDelay(typicalDelayMin)) {
                throw EntityValidationException(
                    "Typical delay must not be negative."
                )
            }

            return Route(
                id = id,
                distanceKm = distanceKm,
                typicalDelayMin = typicalDelayMin,
                originHub = originHub,
                destinationHub = destinationHub
            )
        }
    }
}
