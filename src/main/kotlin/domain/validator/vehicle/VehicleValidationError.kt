package domain.validator.vehicle

import domain.model.exception.DomainException
import domain.validator.ValidationError

sealed interface VehicleValidationError : ValidationError {

    data object InvalidCapacity : VehicleValidationError {
        override val message = DomainException.INVALID_VEHICLE_CAPACITY
    }

    data object InvalidCost : VehicleValidationError {
        override val message = DomainException.INVALID_VEHICLE_COST
    }

    data object NoUpdateFields : VehicleValidationError {
        override val message = DomainException.NO_UPDATE_FIELDS
    }
}
