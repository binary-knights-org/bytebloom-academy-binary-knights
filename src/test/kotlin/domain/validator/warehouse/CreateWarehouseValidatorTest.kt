package domain.validator.warehouse

import domain.model.Warehouse
import domain.model.exception.BlankRegionalZoneException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidWarehouseNameException
import domain.model.input.CreateWarehouseInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CreateWarehouseValidatorTest {

    private val validator = CreateWarehouseValidator()

    private val validWarehouseInput = CreateWarehouseInput(
        id = "WH-001",
        name = "Hub-001",
        regionalZone = "CENTRAL",
        latitude = 37.91,
        longitude = -88.46
    )

    @Test
    fun `should return Valid when all warehouse fields are valid`() {
        val input = validWarehouseInput

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Valid)
    }

    @Test
    fun `should return InvalidWarehouseNameException when warehouse name is blank`() {
        val input = validWarehouseInput.copy(name = "   ")

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidWarehouseNameException })
    }

    @Test
    fun `should return BlankRegionalZoneException when regional zone is blank`() {
        val input = validWarehouseInput.copy(regionalZone = "   ")

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is BlankRegionalZoneException })
    }

    @Test
    fun `should return InvalidLatitudeException when latitude is below minimum`() {
        val input = validWarehouseInput.copy(
            latitude = Warehouse.MIN_LATITUDE - 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLatitudeException })
    }

    @Test
    fun `should return InvalidLatitudeException when latitude is above maximum`() {
        val input = validWarehouseInput.copy(
            latitude = Warehouse.MAX_LATITUDE + 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLatitudeException })
    }

    @Test
    fun `should return InvalidLongitudeException when longitude is below minimum`() {
        val input = validWarehouseInput.copy(
            longitude = Warehouse.MIN_LONGITUDE - 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLongitudeException })
    }

    @Test
    fun `should return InvalidLongitudeException when longitude is above maximum`() {
        val input = validWarehouseInput.copy(
            longitude = Warehouse.MAX_LONGITUDE + 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLongitudeException })
    }

    @Test
    fun `should accept minimum and maximum coordinate boundaries`() {
        val minimumCoordinatesInput = validWarehouseInput.copy(
            latitude = Warehouse.MIN_LATITUDE,
            longitude = Warehouse.MIN_LONGITUDE
        )

        val maximumCoordinatesInput = validWarehouseInput.copy(
            latitude = Warehouse.MAX_LATITUDE,
            longitude = Warehouse.MAX_LONGITUDE
        )

        val minimumValidationResult = validator.validate(minimumCoordinatesInput)
        val maximumValidationResult = validator.validate(maximumCoordinatesInput)

        assertTrue(minimumValidationResult is ValidationResult.Valid)
        assertTrue(maximumValidationResult is ValidationResult.Valid)
    }
}
