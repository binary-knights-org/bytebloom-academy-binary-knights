package domain.validator.vehicle

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidVehicleCapacityException
import domain.model.exception.InvalidVehicleCostException
import domain.model.input.CreateVehicleInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateVehicleValidatorTest {

    private val validator = CreateVehicleValidator()

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    @Test
    fun `should return Valid when vehicle input is valid`() {
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin,
            maxCapacityKg = 7600.0,
            costPerKm = 3.7
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with InvalidVehicleCostException when cost is zero`() {
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin,
            maxCapacityKg = 7600.0,
            costPerKm = 0.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is InvalidVehicleCostException })
    }

    @Test
    fun `should return Invalid with InvalidVehicleCapacityException when capacity is zero`() {
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin,
            maxCapacityKg = 0.0,
            costPerKm = 3.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is InvalidVehicleCapacityException })
    }

    @Test
    fun `should return Invalid with capacity and cost exceptions when both are zero`() {
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin,
            maxCapacityKg = 0.0,
            costPerKm = 0.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertEquals(2, result.violations.size)
        assertTrue(result.violations.any { it is InvalidVehicleCapacityException })
        assertTrue(result.violations.any { it is InvalidVehicleCostException })
    }
}
