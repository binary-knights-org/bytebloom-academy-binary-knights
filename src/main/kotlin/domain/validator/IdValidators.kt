package domain.validator

import domain.validator.warehouse.ValidationResult
import domain.validator.warehouse.ValidationResultBuilder

object IdValidator {

    private val UUID_REGEX = Regex(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    )

    private val WAREHOUSE_PREFIX_REGEX = Regex("^(?i)WH-[A-Za-z0-9_-]+$")
    private val VEHICLE_PREFIX_REGEX = Regex("^(?i)TRK-[A-Za-z0-9_-]+$")
    private val ROUTE_PREFIX_REGEX = Regex("^(?i)(RT|ROUTE)-[A-Za-z0-9_-]+$")
    private val PACKAGE_PREFIX_REGEX = Regex("^(?i)PKG-[A-Za-z0-9_-]+$")

    fun validateWarehouseId(id: String?, fieldName: String = "warehouseId"): ValidationResult =
        validateId(id, fieldName, WAREHOUSE_PREFIX_REGEX, "WH- or UUID")

    fun validateVehicleId(id: String?, fieldName: String = "vehicleId"): ValidationResult =
        validateId(id, fieldName, VEHICLE_PREFIX_REGEX, "TRK- or UUID")

    fun validateRouteId(id: String?, fieldName: String = "routeId"): ValidationResult =
        validateId(id, fieldName, ROUTE_PREFIX_REGEX, "RT-, ROUTE- or UUID")

    fun validatePackageId(id: String?, fieldName: String = "packageId"): ValidationResult =
        validateId(id, fieldName, PACKAGE_PREFIX_REGEX, "PKG- or UUID")

    private fun validateId(
        id: String?,
        fieldName: String,
        prefixRegex: Regex,
        expectedFormat: String
    ): ValidationResult {
        val builder = ValidationResultBuilder()
        if (id.isNullOrBlank()) {
            builder.addViolation(fieldName, "ID must not be null or blank")
            return builder.build()
        }

        val trimmed = id.trim()
        val isValidPrefix = prefixRegex.matches(trimmed)
        val isValidUuid = UUID_REGEX.matches(trimmed)

        builder.check(
            condition = isValidPrefix || isValidUuid,
            field = fieldName,
            message = "ID '$id' has an invalid format. Expected prefix $expectedFormat."
        )

        return builder.build()
    }
}

class ValidateWarehouseIdUseCase {
    operator fun invoke(id: String?): ValidationResult =
        IdValidator.validateWarehouseId(id, "warehouseId")
}


class ValidateVehicleIdUseCase {
    operator fun invoke(id: String?): ValidationResult =
        IdValidator.validateVehicleId(id, "vehicleId")
}


class ValidateRouteIdUseCase {
    operator fun invoke(id: String?): ValidationResult =
        IdValidator.validateRouteId(id, "routeId")
}


class ValidatePackageIdUseCase {
    operator fun invoke(id: String?): ValidationResult =
        IdValidator.validatePackageId(id, "packageId")
}
