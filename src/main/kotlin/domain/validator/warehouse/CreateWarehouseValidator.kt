package domain.validator.warehouse

import domain.model.Warehouse
import domain.model.exception.BlankRegionalZoneException
import domain.model.exception.DomainException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidWarehouseNameException
import domain.model.input.CreateWarehouseInput
import domain.model.validation.ValidationResult

class CreateWarehouseValidator {

    fun validate(input: CreateWarehouseInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (input.name.isBlank()) add(InvalidWarehouseNameException())
            if (input.regionalZone.isBlank()) add(BlankRegionalZoneException())
            if (input.latitude !in Warehouse.MIN_LATITUDE..Warehouse.MAX_LATITUDE) {
                add(InvalidLatitudeException())
            }
            if (input.longitude !in Warehouse.MIN_LONGITUDE..Warehouse.MAX_LONGITUDE) {
                add(InvalidLongitudeException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
