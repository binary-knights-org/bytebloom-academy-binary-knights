package domain.validator

private const val VEHICLE_ID_PREFIX = "TRK-"
private const val MINIMUM_CAPACITY_KG = 0.0
private const val MINIMUM_COST_PER_KM = 0.0

data class VehicleCreateFields(
    val vehicleId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHubId: String
)

data class VehicleUpdateFields(
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentHubId: String? = null
)

object VehicleValidator {

    fun validateForCreate(fields: VehicleCreateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.vehicleId.isBlank()) {
            errors += FieldError("vehicleId", "Vehicle id must not be blank.")
        }
        if (fields.maxCapacityKg <= MINIMUM_CAPACITY_KG) {
            errors += FieldError("maxCapacityKg", "Max capacity must be greater than 0.")
        }
        if (fields.costPerKm <= MINIMUM_COST_PER_KM) {
            errors += FieldError("costPerKm", "Cost per km must be greater than 0.")
        }
        if (fields.currentHubId.isBlank()) {
            errors += FieldError("currentHubId", "Current hub id must not be blank.")
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateForUpdate(fields: VehicleUpdateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.maxCapacityKg == null && fields.costPerKm == null && fields.currentHubId == null) {
            errors += FieldError(
                "update",
                "At least one field (maxCapacityKg, costPerKm, currentHubId) must be provided."
            )
        }

        fields.maxCapacityKg?.let { maxCapacityKg ->
            if (maxCapacityKg <= MINIMUM_CAPACITY_KG) {
                errors += FieldError("maxCapacityKg", "Max capacity must be greater than 0.")
            }
        }
        fields.costPerKm?.let { costPerKm ->
            if (costPerKm <= MINIMUM_COST_PER_KM) {
                errors += FieldError("costPerKm", "Cost per km must be greater than 0.")
            }
        }
        fields.currentHubId?.let { currentHubId ->
            if (currentHubId.isBlank()) {
                errors += FieldError("currentHubId", "Current hub id must not be blank.")
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateId(id: String): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (id.isBlank()) {
            errors += FieldError("id", "Vehicle id must not be blank.")
        } else if (!id.startsWith(VEHICLE_ID_PREFIX) ) {
            errors += FieldError(
                "id",
                "Vehicle id must start with \"$VEHICLE_ID_PREFIX\" or be a valid UUID."
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }
}
