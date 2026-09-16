package domain.validator

import java.util.UUID

private const val PACKAGE_ID_PREFIX = "PKG-"
private const val MINIMUM_WEIGHT = 0.0

private val ALLOWED_PRIORITIES = setOf("URGENT", "STANDARD", "LOW")

data class PackageCreateFields(
    val packageId: String,
    val weight: Double,
    val originHubId: String,
    val destinationHubId: String,
    val priority: String
)

data class PackageUpdateFields(
    val weight: Double? = null,
    val destinationHubId: String? = null,
    val priority: String? = null
)

object PackageValidator {

    fun validateForCreate(fields: PackageCreateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.packageId.isBlank()) {
            errors += FieldError("packageId", "Package id must not be blank.")
        }
        if (fields.weight <= MINIMUM_WEIGHT) {
            errors += FieldError("weight", "Weight must be greater than 0.")
        }
        if (fields.originHubId.isBlank()) {
            errors += FieldError("originHubId", "Origin hub id must not be blank.")
        }
        if (fields.destinationHubId.isBlank()) {
            errors += FieldError("destinationHubId", "Destination hub id must not be blank.")
        }
        if (fields.priority.uppercase() !in ALLOWED_PRIORITIES) {
            errors += FieldError(
                "priority", "Priority must be one of ${ALLOWED_PRIORITIES.joinToString()}."
            )
        }
        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateForUpdate(fields: PackageUpdateFields): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (fields.weight == null && fields.destinationHubId == null && fields.priority == null) {
            errors += FieldError(
                "update", "At least one field (weight, destinationHubId, priority) must be provided."
            )
        }

        fields.weight?.let { weight ->
            if (weight <= 0.0) {
                errors += FieldError("weight", "Weight must be greater than 0.")
            }
        }
        fields.destinationHubId?.let { destinationHubId ->
            if (destinationHubId.isBlank()) {
                errors += FieldError("destinationHubId", "Destination hub id must not be blank.")
            }
        }
        fields.priority?.let { priority ->
            if (priority.uppercase() !in ALLOWED_PRIORITIES) {
                errors += FieldError(
                    "priority", "Priority must be one of ${ALLOWED_PRIORITIES.joinToString()}."
                )
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

    fun validateId(id: String): ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (id.isBlank()) {
            errors += FieldError("id", "Package id must not be blank.")
        } else if (!id.startsWith(PACKAGE_ID_PREFIX)) {
            errors += FieldError(
                "id", "Package id must start with \"$PACKAGE_ID_PREFIX\" or be a valid UUID."
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Failure(errors)
    }

}
