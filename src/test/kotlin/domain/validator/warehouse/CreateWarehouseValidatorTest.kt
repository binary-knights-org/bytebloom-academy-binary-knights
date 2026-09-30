package domain.validator.warehouse

import domain.model.input.CreateWarehouseInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
    fun `should return valid when all warehouse fields are valid`() {
        // Given
        val input = validWarehouseInput

        // When
        val validationResult = validator.validate(input)

        // Then
        assertTrue(validationResult is ValidationResult.Valid)
    }

    @Test
    fun `should return blank name violation when warehouse name is blank`() {
        // Given
        val input = validWarehouseInput.copy(name = "   ")

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
        val input = validWarehouseInput.copy(regionalZone = "   ")

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
        val input = validWarehouseInput.copy(
            latitude = CreateWarehouseValidator.MIN_LATITUDE - 0.1
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
        val input = validWarehouseInput.copy(
            latitude = CreateWarehouseValidator.MAX_LATITUDE + 0.1
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
        val input = validWarehouseInput.copy(
            longitude = CreateWarehouseValidator.MIN_LONGITUDE - 0.1
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
        val input = validWarehouseInput.copy(
            longitude = CreateWarehouseValidator.MAX_LONGITUDE + 0.1
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
    fun `should accept minimum and maximum coordinate boundaries`() {
        // Given
        val minimumCoordinatesInput = validWarehouseInput.copy(
            latitude = CreateWarehouseValidator.MIN_LATITUDE,
            longitude = CreateWarehouseValidator.MIN_LONGITUDE
        )

        val maximumCoordinatesInput = validWarehouseInput.copy(
            latitude = CreateWarehouseValidator.MAX_LATITUDE,
            longitude = CreateWarehouseValidator.MAX_LONGITUDE
        )

        // When
        val minimumValidationResult = validator.validate(minimumCoordinatesInput)
        val maximumValidationResult = validator.validate(maximumCoordinatesInput)

        // Then
        assertTrue(minimumValidationResult is ValidationResult.Valid)
        assertTrue(maximumValidationResult is ValidationResult.Valid)
    }
}

