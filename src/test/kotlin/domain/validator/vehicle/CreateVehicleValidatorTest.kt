package domain.validator.vehicle

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.CreateVehicleInput
import domain.model.validation.ValidationResult
import kotlin.test.Test
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
    fun `should return valid when vehicle input is valid`() {

        // Given
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin ,
            maxCapacityKg = 7600.0,
            costPerKm = 3.7
            )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return invalid with Invalid Cost Per Km when cost is zero`() {

        // Given
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin ,
            maxCapacityKg = 7600.0,
            costPerKm = 0.0
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
    fun `should return invalid with Invalid max capacity when cost is zero`() {

        // Given
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin ,
            maxCapacityKg = 0.0,
            costPerKm = 3.0
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
    fun `should return invalid with Invalid max capacity and Invalid Cost when cost and max capacity is zero`() {

        // Given
        val input = CreateVehicleInput(
            id = "TRK-1",
            currentHub = origin ,
            maxCapacityKg = 0.0,
            costPerKm = 0.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid
        )

        assertEquals(
            listOf(VehicleValidationError.InvalidCapacity
            , VehicleValidationError.InvalidCost),
            result.violations
        )

    }
}
