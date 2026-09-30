package domain.validator.vehicle

import domain.model.RegionalZone
import domain.model.Warehouse
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
    fun `should return valid when update contains valid field`() {

        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            currentHub = origin ,
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with NoUpdateFields when no fields are provided`() {

        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1"
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid
        )
        assertEquals(
            listOf(VehicleValidationError.NoUpdateFields),
            result.violations)

    }

    @Test
    fun `should return Invalid with InvalidCost when costPerKm is zero`() {

        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            costPerKm =  0.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid
        )
        assertEquals(
            listOf(VehicleValidationError.InvalidCost),
            result.violations
            )

    }


    @Test
    fun `should return Invalid with Invalid Max Capacity when max capacity is zero`() {

        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            maxCapacityKg = 0.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid
        )
        assertEquals(
            listOf(VehicleValidationError.InvalidCapacity),
            result.violations
        )
    }
    @Test
    fun `should return multiple violations when multiple update rules are violated`() {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            maxCapacityKg = 0.0,
            costPerKm =  0.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Invalid)

        assertEquals(
            listOf(
                VehicleValidationError.InvalidCapacity,
                VehicleValidationError.InvalidCost
            ),
            result.violations
        )
    }
}
