package domain.validator.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.CreatePackageInput
import domain.model.validation.ValidationResult
import kotlin.test.Test
import kotlin.test.assertTrue

class CreatePackageValidatorTest {

    private val validator = CreatePackageValidator()

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val destination = Warehouse(
        id = "WH-2",
        name = "Destination Warehouse",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    @Test
    fun `should return valid when package input is valid`() {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return invalid when weight is zero`() {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 0.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid &&
                    PackageValidationError.InvalidWeight in result.violations
        )
    }

    @Test
    fun `should return invalid when origin and destination are the same`() {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = origin
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid &&
                    PackageValidationError.SameOriginAndDestination in result.violations
        )
    }
}
