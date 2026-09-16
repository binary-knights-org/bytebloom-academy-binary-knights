package domain.validator

private const val ROUTE_ID_PREFIX = "RT-"
private const val MINIMUM_DISTANCE_KM = 0.0
private const val MINIMUM_DELAY_MIN = 0


data class RouteCreateFields(
    val routeId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originHubId: String,
    val destinationHubId: String
)

data class RouteUpdateFields(
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null,
    val destinationHubId: String? = null
)

object RouteValidator {

    fun validateForCreate(fields: RouteCreateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.routeId.isBlank()) {
            errors += FieldError("routeId", "Route id must not be blank.")
        }
        if (fields.distanceKm <= MINIMUM_DISTANCE_KM) {
            errors += FieldError("distanceKm", "Distance must be greater than 0.")
        }
        if (fields.typicalDelayMin < MINIMUM_DELAY_MIN) {
            errors += FieldError("typicalDelayMin", "Typical delay must not be negative.")
        }
        if (fields.originHubId.isBlank()) {
            errors += FieldError("originHubId", "Origin hub id must not be blank.")
        }
        if (fields.destinationHubId.isBlank()) {
            errors += FieldError("destinationHubId", "Destination hub id must not be blank.")
        }
        if (fields.originHubId.isNotBlank() && fields.originHubId == fields.destinationHubId) {
            errors += FieldError(
                "destinationHubId",
                "Destination hub must be different from origin hub."
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateForUpdate(fields: RouteUpdateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.distanceKm == null && fields.typicalDelayMin == null && fields.destinationHubId == null) {
            errors += FieldError(
                "update",
                "At least one field (distanceKm, typicalDelayMin, destinationHubId) must be provided."
            )
        }

        fields.distanceKm?.let { distanceKm ->
            if (distanceKm <= MINIMUM_DISTANCE_KM) {
                errors += FieldError("distanceKm", "Distance must be greater than 0.")
            }
        }
        fields.typicalDelayMin?.let { typicalDelayMin ->
            if (typicalDelayMin < MINIMUM_DELAY_MIN) {
                errors += FieldError("typicalDelayMin", "Typical delay must not be negative.")
            }
        }
        fields.destinationHubId?.let { destinationHubId ->
            if (destinationHubId.isBlank()) {
                errors += FieldError("destinationHubId", "Destination hub id must not be blank.")
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateId(id: String): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (id.isBlank()) {
            errors += FieldError("id", "Route id must not be blank.")
        } else if (!id.startsWith(ROUTE_ID_PREFIX) ) {
            errors += FieldError(
                "id",
                "Route id must start with \"$ROUTE_ID_PREFIX\" or be a valid UUID."
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }
}
