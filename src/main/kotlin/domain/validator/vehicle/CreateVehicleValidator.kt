package domain.validator.vehicle

import domain.model.Vehicle
import domain.model.exception.DomainException
import domain.model.exception.InvalidVehicleCapacityException
import domain.model.exception.InvalidVehicleCostException
import domain.model.input.CreateVehicleInput
import domain.model.validation.ValidationResult

class CreateVehicleValidator {

    fun validate(input: CreateVehicleInput): ValidationResult<DomainException> {
        val violations = buildList {
            if (input.maxCapacityKg <= Vehicle.MIN_CAPACITY_KG) {
                add(InvalidVehicleCapacityException())
            }
            if (input.costPerKm <= Vehicle.MIN_COST_PER_KM) {
                add(InvalidVehicleCostException())
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
