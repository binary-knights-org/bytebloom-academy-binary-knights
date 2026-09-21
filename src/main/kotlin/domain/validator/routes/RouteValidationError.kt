package domain.validator.routes

import domain.model.exception.DomainException
import domain.validator.ValidationError

sealed interface RouteValidationError : ValidationError {

    data object InvalidDistance : RouteValidationError {
        override val message = DomainException.INVALID_ROUTE_DISTANCE
    }

    data object NegativeDelay : RouteValidationError {
        override val message = DomainException.INVALID_ROUTE_DELAY
    }

    data object SameOriginAndDestination : RouteValidationError {
        override val message = DomainException.SAME_ORIGIN_DESTINATION
    }

    data object NoUpdateFields : RouteValidationError {
        override val message = DomainException.NO_UPDATE_FIELDS
    }
}
