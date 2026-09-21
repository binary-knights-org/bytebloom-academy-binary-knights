package domain.validator.warehouse

import domain.model.exception.DomainException
import domain.validator.ValidationError

sealed interface WarehouseValidationError : ValidationError {

    data object BlankName : WarehouseValidationError {
        override val message = DomainException.BLANK_WAREHOUSE_NAME
    }

    data object BlankRegionalZone : WarehouseValidationError {
        override val message = DomainException.BLANK_REGIONAL_ZONE
    }

    data object InvalidLatitude : WarehouseValidationError {
        override val message = DomainException.INVALID_LATITUDE
    }

    data object InvalidLongitude : WarehouseValidationError {
        override val message = DomainException.INVALID_LONGITUDE
    }

    data object NoUpdateFields : WarehouseValidationError {
        override val message = DomainException.NO_UPDATE_FIELDS
    }
}
