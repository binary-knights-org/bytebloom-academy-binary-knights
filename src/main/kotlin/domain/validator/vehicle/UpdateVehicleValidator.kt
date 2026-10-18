package domain.validator.vehicle

import domain.model.Vehicle
import domain.model.exception.DomainException
import domain.model.exception.InvalidVehicleCapacityException
import domain.model.exception.InvalidVehicleCostException
import domain.model.exception.NoUpdateFieldsException
import domain.model.input.UpdateVehicleInput
import domain.model.validation.ValidationResult

class UpdateVehicleValidator {

    fun validate(input: UpdateVehicleInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (hasNoUpdates(input)) {
                add(NoUpdateFieldsException())
            }

            input.maxCapacityKg?.let { capacity ->
                if (capacity <= Vehicle.MIN_CAPACITY_KG) add(InvalidVehicleCapacityException())
            }

            input.costPerKm?.let { cost ->
                if (cost <= Vehicle.MIN_COST_PER_KM) add(InvalidVehicleCostException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    private fun hasNoUpdates(input: UpdateVehicleInput): Boolean {
        return input.maxCapacityKg == null &&
                input.costPerKm == null &&
                input.currentHub == null
    }
}
