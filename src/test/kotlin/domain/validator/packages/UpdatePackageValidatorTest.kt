package domain.validator.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.UpdatePackageInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
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

    @Test
    fun `should return Valid when update contains valid field`() {
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
    fun `should return Invalid with NoUpdateFields when no fields are provided`() {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1"
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Invalid)

        assertEquals(
            listOf(PackageValidationError.NoUpdateFields),
            result.violations
        )
    }

    @Test
    fun `should return Invalid with InvalidWeight when weight is zero`() {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 0.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Invalid)

        assertEquals(
            listOf(PackageValidationError.InvalidWeight),
            result.violations
        )
    }

    @Test
    fun `should return Invalid with SameOriginAndDestination when hubs are the same`() {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            originHub = origin,
            destinationHub = origin
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Invalid)

        assertEquals(
            listOf(PackageValidationError.SameOriginAndDestination),
            result.violations
        )
    }

    @Test
    fun `should return multiple violations when multiple update rules are violated`() {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 0.0,
            originHub = origin,
            destinationHub = origin
        )

        // When
        val result = validator.validate(input)

        // Then
        assertTrue(result is ValidationResult.Invalid)

        assertEquals(
            listOf(
                PackageValidationError.InvalidWeight,
                PackageValidationError.SameOriginAndDestination
            ),
            result.violations
        )
    }
}
