package domain.validator.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.NoUpdateFieldsException
import domain.model.exception.SameOriginAndDestinationException
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
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with NoUpdateFieldsException when no fields are provided`() {
        val input = UpdatePackageInput(id = "PKG-1")

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is NoUpdateFieldsException })
    }

    @Test
    fun `should return Invalid with InvalidPackageWeightException when weight is zero`() {
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 0.0
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is InvalidPackageWeightException })
    }

    @Test
    fun `should return Invalid with SameOriginAndDestinationException when hubs are the same`() {
        val input = UpdatePackageInput(
            id = "PKG-1",
            originHub = origin,
            destinationHub = origin
        )

        val result = validator.validate(input)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue(result.violations.any { it is SameOriginAndDestinationException })
    }

    @Test
    fun `should return multiple violations when multiple update rules are violated`() {
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 0.0,
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
