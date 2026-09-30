package domain.validator.warehouse

import domain.model.input.UpdateWarehouseInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdateWarehouseValidatorTest {

    private val validator = UpdateWarehouseValidator()

    private val validWarehouseUpdate = UpdateWarehouseInput(
        name = "Updated Warehouse",
        regionalZone = "CENTRAL",
        latitude = 37.91,
        longitude = -88.46
    )

    @Test
    fun `should return valid for valid update`() {
        // Given
        val input = validWarehouseUpdate

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Valid)
    }

    @Test
    fun `should return no update fields violation when all fields are null`() {
        // Given
        val input = UpdateWarehouseInput()

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.NoUpdateFields)
        )
    }

    @Test
    fun `should return blank name violation when name is blank`() {
        // Given
        val input = validWarehouseUpdate.copy(name = "   ")

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.BlankName)
        )
    }

    @Test
    fun `should return blank regional zone violation when regional zone is blank`() {
        // Given
        val input = validWarehouseUpdate.copy(regionalZone = "   ")

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.BlankRegionalZone)
        )
    }

    @Test
    fun `should return invalid latitude violation when latitude is below minimum`() {
        // Given
        val input = validWarehouseUpdate.copy(
            latitude = UpdateWarehouseValidator.MIN_LATITUDE - 0.1
        )

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.InvalidLatitude)
        )
    }

    @Test
    fun `should return invalid latitude violation when latitude is above maximum`() {
        // Given
        val input = validWarehouseUpdate.copy(
            latitude = UpdateWarehouseValidator.MAX_LATITUDE + 0.1
        )

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.InvalidLatitude)
        )
    }

    @Test
    fun `should return invalid longitude violation when longitude is below minimum`() {
        // Given
        val input = validWarehouseUpdate.copy(
            longitude = UpdateWarehouseValidator.MIN_LONGITUDE - 0.1
        )

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.InvalidLongitude)
        )
    }

    @Test
    fun `should return invalid longitude violation when longitude is above maximum`() {
        // Given
        val input = validWarehouseUpdate.copy(
            longitude = UpdateWarehouseValidator.MAX_LONGITUDE + 0.1
        )

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.InvalidLongitude)
        )
    }

    @Test
    fun `should accept coordinate boundary values`() {
        // Given
        val minBoundary = validWarehouseUpdate.copy(
            latitude = UpdateWarehouseValidator.MIN_LATITUDE,
            longitude = UpdateWarehouseValidator.MIN_LONGITUDE
        )

        val maxBoundary = validWarehouseUpdate.copy(
            latitude = UpdateWarehouseValidator.MAX_LATITUDE,
            longitude = UpdateWarehouseValidator.MAX_LONGITUDE
        )

        // When
        val minResult = validator.validate(minBoundary)
        val maxResult = validator.validate(maxBoundary)

        // Then
        assertTrue(minResult is ValidationResult.Valid)
        assertTrue(maxResult is ValidationResult.Valid)
    }

    @Test
    fun `should return all applicable violations when multiple fields are invalid`() {
        // Given
        val input = UpdateWarehouseInput(
            name = "   ",
            regionalZone = "   ",
            latitude = UpdateWarehouseValidator.MAX_LATITUDE + 0.1,
            longitude = UpdateWarehouseValidator.MIN_LONGITUDE - 0.1
        )

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Invalid)

        val violations = (validationResult as ValidationResult.Invalid).violations

        assertTrue(
            violations.contains(WarehouseValidationError.BlankName)
        )
        assertTrue(
            violations.contains(WarehouseValidationError.BlankRegionalZone)
        )
        assertTrue(
            violations.contains(WarehouseValidationError.InvalidLatitude)
        )
        assertTrue(
            violations.contains(WarehouseValidationError.InvalidLongitude)
        )
    }
}
