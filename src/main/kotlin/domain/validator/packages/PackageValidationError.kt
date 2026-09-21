package domain.validator.packages

import domain.model.exception.DomainException
import domain.validator.ValidationError

sealed interface PackageValidationError : ValidationError {

    data object InvalidWeight : PackageValidationError {
        override val message = DomainException.INVALID_PACKAGE_WEIGHT
    }

    data object SameOriginAndDestination : PackageValidationError {
        override val message = DomainException.SAME_ORIGIN_DESTINATION
    }

    data object NoUpdateFields : PackageValidationError {
        override val message = DomainException.NO_UPDATE_FIELDS
    }
}
