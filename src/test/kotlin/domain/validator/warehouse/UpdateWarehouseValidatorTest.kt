package domain.validator.warehouse

import domain.model.Warehouse
import domain.model.exception.BlankRegionalZoneException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidWarehouseNameException
import domain.model.exception.NoUpdateFieldsException
import domain.model.input.UpdateWarehouseInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class UpdateWarehouseValidatorTest {

    private val validator = UpdateWarehouseValidator()

    private val validWarehouseUpdate = UpdateWarehouseInput(
        id = "WH-001",
        name = "Updated Warehouse",
        regionalZone = "CENTRAL",
        latitude = 37.91,
        longitude = -88.46
    )

    @Test
    fun `should return Valid for valid update`() {
        val input = validWarehouseUpdate

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Valid)
    }

    @Test
    fun `should return NoUpdateFieldsException when all optional fields are null`() {
        val input = UpdateWarehouseInput(id = "WH-001")

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is NoUpdateFieldsException })
    }

    @Test
    fun `should return InvalidWarehouseNameException when name is blank`() {
        val input = validWarehouseUpdate.copy(name = "   ")

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidWarehouseNameException })
    }

    @Test
    fun `should return BlankRegionalZoneException when regional zone is blank`() {
        val input = validWarehouseUpdate.copy(regionalZone = "   ")

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is BlankRegionalZoneException })
    }

    @Test
    fun `should return InvalidLatitudeException when latitude is below minimum`() {
        val input = validWarehouseUpdate.copy(
            latitude = Warehouse.MIN_LATITUDE - 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLatitudeException })
    }

    @Test
    fun `should return InvalidLatitudeException when latitude is above maximum`() {
        val input = validWarehouseUpdate.copy(
            latitude = Warehouse.MAX_LATITUDE + 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLatitudeException })
    }

    @Test
    fun `should return InvalidLongitudeException when longitude is below minimum`() {
        val input = validWarehouseUpdate.copy(
            longitude = Warehouse.MIN_LONGITUDE - 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLongitudeException })
    }

    @Test
    fun `should return InvalidLongitudeException when longitude is above maximum`() {
        val input = validWarehouseUpdate.copy(
            longitude = Warehouse.MAX_LONGITUDE + 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidLongitudeException })
    }

    @Test
    fun `should accept coordinate boundary values`() {
        val minBoundary = validWarehouseUpdate.copy(
            latitude = Warehouse.MIN_LATITUDE,
            longitude = Warehouse.MIN_LONGITUDE
        )

        val maxBoundary = validWarehouseUpdate.copy(
            latitude = Warehouse.MAX_LATITUDE,
            longitude = Warehouse.MAX_LONGITUDE
        )

        val minResult = validator.validate(minBoundary)
        val maxResult = validator.validate(maxBoundary)

        assertTrue(minResult is ValidationResult.Valid)
        assertTrue(maxResult is ValidationResult.Valid)
    }

    @Test
    fun `should return all applicable violations when multiple fields are invalid`() {
        val input = UpdateWarehouseInput(
            id = "WH-001",
            name = "   ",
            regionalZone = "   ",
            latitude = Warehouse.MAX_LATITUDE + 0.1,
            longitude = Warehouse.MIN_LONGITUDE - 0.1
        )

        val validationResult = validator.validate(input)

        assertTrue(validationResult is ValidationResult.Invalid)
        val violations = (validationResult).violations
        assertTrue(violations.any { it is InvalidWarehouseNameException })
        assertTrue(violations.any { it is BlankRegionalZoneException })
        assertTrue(violations.any { it is InvalidLatitudeException })
        assertTrue(violations.any { it is InvalidLongitudeException })
    }
}
