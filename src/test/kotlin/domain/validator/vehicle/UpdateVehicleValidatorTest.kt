package domain.validator.vehicle

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidVehicleCapacityException
import domain.model.exception.InvalidVehicleCostException
import domain.model.exception.NoUpdateFieldsException
import domain.model.input.UpdateVehicleInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateVehicleValidatorTest {

    private val validator = UpdateVehicleValidator()

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    @Test
    fun `should return Valid when update contains valid field`() {
        val input = UpdateVehicleInput(
            id = "TRK-1",
            currentHub = origin
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with NoUpdateFieldsException when no fields are provided`() {
        val input = UpdateVehicleInput(id = "TRK-1")

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is NoUpdateFieldsException })
    }

    @Test
    fun `should return Invalid with InvalidVehicleCostException when costPerKm is zero`() {
        val input = UpdateVehicleInput(
            id = "TRK-1",
            costPerKm = 0.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is InvalidVehicleCostException })
    }

    @Test
    fun `should return Invalid with InvalidVehicleCapacityException when max capacity is zero`() {
        val input = UpdateVehicleInput(
            id = "TRK-1",
            maxCapacityKg = 0.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is InvalidVehicleCapacityException })
    }

    @Test
    fun `should return multiple violations when multiple update rules are violated`() {
        val input = UpdateVehicleInput(
            id = "TRK-1",
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
