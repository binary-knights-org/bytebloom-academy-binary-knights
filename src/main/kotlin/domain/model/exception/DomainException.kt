package domain.model.exception

sealed class DomainException(message: String) : Exception(message)

class BlankIdException(entityName: String) :
    DomainException("$entityName ID cannot be blank.")

class InvalidIdFormatException(entityName: String, prefix: String) :
    DomainException("$entityName ID must start with '$prefix' or be a valid UUID.")

class InvalidWeightException(minWeight: Double) :
    DomainException("Weight must be greater than $minWeight.")

class NoUpdateFieldsException(updatableFields: String) :
    DomainException("At least one field ($updatableFields) must be provided for update.")

class SameOriginDestinationException :
    DomainException("Destination hub must be different from origin hub.")

class EntityNotFoundException(entityName: String, id: String) :
    DomainException("$entityName with ID '$id' was not found.")

class DatabaseOperationFailedException(operation: String, entityName: String) :
    DomainException("Failed to $operation $entityName in database.")

class BlankFieldException(fieldName: String) :
    DomainException("$fieldName cannot be blank.")

class InvalidCoordinateException(coordinateName: String, min: Double, max: Double) :
    DomainException("$coordinateName must be between $min and $max.")

class InvalidDistanceException(minDistance: Double) :
    DomainException("Distance must be greater than $minDistance.")

class InvalidDelayException(minDelay: Int) :
    DomainException("Typical delay must be at least $minDelay.")

class InvalidMaxCapacityException(minCapacity: Double) :
    DomainException("Max capacity must be greater than $minCapacity.")

class InvalidCostPerKmException(minCost: Double) :
    DomainException("Cost per km must be greater than $minCost.")
