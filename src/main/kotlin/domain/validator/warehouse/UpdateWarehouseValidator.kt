package domain.validator.warehouse

import domain.model.Warehouse
import domain.model.exception.BlankRegionalZoneException
import domain.model.exception.DomainException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidWarehouseNameException
import domain.model.exception.NoUpdateFieldsException
import domain.model.input.UpdateWarehouseInput
import domain.model.validation.ValidationResult

class UpdateWarehouseValidator {

    fun validate(input: UpdateWarehouseInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(NoUpdateFieldsException())
            }

            input.name?.let { if (it.isBlank()) add(InvalidWarehouseNameException()) }
            if (input.regionalZone?.isBlank() == true) add(BlankRegionalZoneException())

            input.latitude?.let { lat ->
                if (lat !in Warehouse.MIN_LATITUDE..Warehouse.MAX_LATITUDE) add(InvalidLatitudeException())
            }
            input.longitude?.let { lon ->
                if (lon !in Warehouse.MIN_LONGITUDE..Warehouse.MAX_LONGITUDE) add(InvalidLongitudeException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdateWarehouseInput): Boolean {
        return input.name == null &&
                input.regionalZone == null &&
                input.latitude == null &&
                input.longitude == null
    }
}
