package domain.validator.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.CreatePackageInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
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
    fun `should return Valid when package input is valid`() {
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with InvalidPackageWeightException when weight is zero`() {
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 0.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is InvalidPackageWeightException })
    }

    @Test
    fun `should return Invalid with SameOriginAndDestinationException when hubs are the same`() {
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = origin
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is SameOriginAndDestinationException })
    }

    @Test
    fun `should return both violations when weight is invalid and hubs are the same`() {
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 0.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = origin
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertEquals(2, result.violations.size)
        assertTrue(result.violations.any { it is InvalidPackageWeightException })
        assertTrue(result.violations.any { it is SameOriginAndDestinationException })
    }
}
