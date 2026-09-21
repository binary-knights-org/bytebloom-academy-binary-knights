package domain.model.exception

import domain.validator.ValidationError

sealed class DomainException(message: String) : Exception(message) {
    companion object {
        const val RESOURCE_NOT_FOUND = "Requested resource was not found."
        const val DATA_UNAVAILABLE = "Data source is currently unavailable."
        const val OPERATION_FAILED = "Operation failed."
        const val NO_UPDATE_FIELDS = "At least one field must be provided for update."

        const val INVALID_PACKAGE_WEIGHT = "Weight must be greater than 0.0"
        const val SAME_ORIGIN_DESTINATION = "Destination hub must be different from origin hub."

        const val INVALID_ROUTE_DISTANCE = "distanceKm must be greater than 0.0."
        const val INVALID_ROUTE_DELAY = "typicalDelayMin must not be negative."

        const val INVALID_VEHICLE_CAPACITY = "maxCapacityKg must be greater than 0.0"
        const val INVALID_VEHICLE_COST = "costPerKm must be greater than 0.0"

        const val BLANK_WAREHOUSE_NAME = "Warehouse name cannot be blank."
        const val BLANK_REGIONAL_ZONE = "Warehouse regional zone cannot be blank."
        const val INVALID_LATITUDE = "Latitude must be between -90.0 and 90.0."
        const val INVALID_LONGITUDE = "Longitude must be between -180.0 and 180.0."
    }
}

class EntityValidationException(val violations: List<ValidationError>) :
    DomainException("Validation failed: ${violations.joinToString("; ") { it.message }}")

class ResourceNotFoundException(message: String = RESOURCE_NOT_FOUND) : DomainException(message)
class DataUnavailableException(message: String = DATA_UNAVAILABLE) : DomainException(message)
class OperationFailedException(message: String = OPERATION_FAILED) : DomainException(message)

class InvalidPackageWeightException(message: String = INVALID_PACKAGE_WEIGHT) : DomainException(message)
class SameOriginAndDestinationException(message: String = SAME_ORIGIN_DESTINATION) : DomainException(message)
class InvalidRouteDistanceException(message: String = INVALID_ROUTE_DISTANCE) : DomainException(message)
class InvalidRouteDelayException(message: String = INVALID_ROUTE_DELAY) : DomainException(message)
class InvalidVehicleCapacityException(message: String = INVALID_VEHICLE_CAPACITY) : DomainException(message)
class InvalidVehicleCostException(message: String = INVALID_VEHICLE_COST) : DomainException(message)
class InvalidWarehouseTextException(message: String = BLANK_WAREHOUSE_NAME) : DomainException(message)
class InvalidLatitudeException(message: String = INVALID_LATITUDE) : DomainException(message)
class InvalidLongitudeException(message: String = INVALID_LONGITUDE) : DomainException(message)
