package domain.validator.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.UpdatePackageInput
import domain.validator.ValidationResult
import kotlin.test.Test
import kotlin.test.assertTrue

class UpdatePackageValidatorTest {

    private val validator = UpdatePackageValidator()

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
    fun `should return valid when update contains valid field`() {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return invalid when no update fields are provided`() {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-1"
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(
            result is ValidationResult.Invalid &&
                    PackageValidationError.NoUpdateFields in result.violations
        )
    }

    @Test
    fun `should return invalid when weight is zero`() {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 0.0
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
        val input = UpdatePackageInput(
            id = "PKG-1",
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
