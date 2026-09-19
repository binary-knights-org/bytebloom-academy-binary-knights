package domain.exception

sealed class DomainValidationException(message: String) : Exception(message)

class BlankIdException(entityName: String) :
    DomainValidationException("$entityName ID cannot be blank.")

class InvalidIdFormatException(entityName: String, prefix: String) :
    DomainValidationException("$entityName ID must start with '$prefix' or be a valid UUID.")

class InvalidWeightException(minWeight: Double) :
    DomainValidationException("Weight must be greater than $minWeight.")

class NoUpdateFieldsException(updatableFields: String) :
    DomainValidationException("At least one field ($updatableFields) must be provided for update.")

class SameOriginDestinationException :
    DomainValidationException("Destination hub must be different from origin hub.")

class EntityNotFoundException(entityName: String, id: String) :
    DomainValidationException("$entityName with ID '$id' was not found.")

class DatabaseOperationFailedException(operation: String, entityName: String) :
    DomainValidationException("Failed to $operation $entityName in database.")

class BlankFieldException(fieldName: String) :
    DomainValidationException("$fieldName cannot be blank.")

class InvalidCoordinateException(coordinateName: String, min: Double, max: Double) :
    DomainValidationException("$coordinateName must be between $min and $max.")

class InvalidDistanceException(minDistance: Double) :
    DomainValidationException("Distance must be greater than $minDistance.")

class InvalidDelayException(minDelay: Int) :
    DomainValidationException("Typical delay must be at least $minDelay.")

class InvalidMaxCapacityException(minCapacity: Double) :
    DomainValidationException("Max capacity must be greater than $minCapacity.")

class InvalidCostPerKmException(minCost: Double) :
    DomainValidationException("Cost per km must be greater than $minCost.")
