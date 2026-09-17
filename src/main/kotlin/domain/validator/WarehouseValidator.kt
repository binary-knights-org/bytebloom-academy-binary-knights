package domain.validator

private const val WAREHOUSE_ID_PREFIX = "WH-"

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

data class WarehouseCreateFields(
    val hubId: String,
    val hubName: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)

data class WarehouseUpdateFields(
    val hubName: String? = null,
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

object WarehouseValidator {

    fun validateForCreate(fields: WarehouseCreateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.hubId.isBlank()) {
            errors += FieldError("hubId", "Warehouse id must not be blank.")
        }
        if (fields.hubName.isBlank()) {
            errors += FieldError("hubName", "Warehouse name must not be blank.")
        }
        if (fields.regionalZone.isBlank()) {
            errors += FieldError("regionalZone", "Regional zone must not be blank.")
        }
        if (fields.latitude !in MIN_LATITUDE..MAX_LATITUDE) {
            errors += FieldError(
                "latitude", "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."
            )
        }
        if (fields.longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
            errors += FieldError(
                "longitude", "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateForUpdate(fields: WarehouseUpdateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.hasNoUpdatedFields()) {
            errors += FieldError(
                "update",
                "At least one field (hubName, regionalZone, latitude, longitude) must be provided."
            )
        }

        fields.hubName?.let { hubName ->
            if (hubName.isBlank()) {
                errors += FieldError("hubName", "Warehouse name must not be blank.")
            }
        }
        fields.regionalZone?.let { regionalZone ->
            if (regionalZone.isBlank()) {
                errors += FieldError("regionalZone", "Regional zone must not be blank.")
            }
        }
        fields.latitude?.let { latitude ->
            if (latitude !in MIN_LATITUDE..MAX_LATITUDE) {
                errors += FieldError(
                    "latitude", "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."
                )
            }
        }
        fields.longitude?.let { longitude ->
            if (longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
                errors += FieldError(
                    "longitude", "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
                )
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateId(id: String): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (id.isBlank()) {
            errors += FieldError("id", "Warehouse id must not be blank.")
        } else if (!id.startsWith(WAREHOUSE_ID_PREFIX)) {
            errors += FieldError(
                "id", "Warehouse id must start with \"$WAREHOUSE_ID_PREFIX\"."
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }
}

private fun WarehouseUpdateFields.hasNoUpdatedFields(): Boolean =
    hubName == null && regionalZone == null && latitude == null && longitude == null
