package domain.validator.routes

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidRouteDelayException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.NoUpdateFieldsException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.UpdateRouteInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test

class UpdateRouteValidatorTest {

    private val validator = UpdateRouteValidator()

    private val originHub = Warehouse(
        id = "WH-ORIGIN",
        name = "Origin Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 10.0,
        longitude = 20.0
    )

    private val destinationHub = Warehouse(
        id = "WH-DEST",
        name = "Destination Hub",
        regionalZone = RegionalZone.SOUTH,
        latitude = 30.0,
        longitude = 40.0
    )

    @Test
    fun `should return Invalid with NoUpdateFieldsException when no fields are provided`() {
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = null,
            typicalDelayMin = null,
            originHub = null,
            destinationHub = null
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is NoUpdateFieldsException }).isTrue()
    }

    @Test
    fun `should return Valid when only valid distanceKm is provided`() {
        val input = UpdateRouteInput(id = "RT-001", distanceKm = 120.5)

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Valid when only valid typicalDelayMin is provided`() {
        val input = UpdateRouteInput(id = "RT-001", typicalDelayMin = 25)

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Valid when typicalDelayMin is zero`() {
        val input = UpdateRouteInput(id = "RT-001", typicalDelayMin = 0)

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with InvalidRouteDistanceException when distanceKm is zero`() {
        val input = UpdateRouteInput(id = "RT-001", distanceKm = 0.0)

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is InvalidRouteDistanceException }).isTrue()
    }

    @Test
    fun `should return Invalid with InvalidRouteDistanceException when distanceKm is negative`() {
        val input = UpdateRouteInput(id = "RT-001", distanceKm = -15.0)

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is InvalidRouteDistanceException }).isTrue()
    }

    @Test
    fun `should return Invalid with InvalidRouteDelayException when typicalDelayMin is negative`() {
        val input = UpdateRouteInput(id = "RT-001", typicalDelayMin = -3)

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is InvalidRouteDelayException }).isTrue()
    }

    @Test
    fun `should return Invalid with SameOriginAndDestinationException when both hubs have same id`() {
        val input = UpdateRouteInput(
            id = "RT-001",
            originHub = originHub,
            destinationHub = originHub.copy()
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is SameOriginAndDestinationException }).isTrue()
    }

    @Test
    fun `should return Valid when both hubs are provided with different ids`() {
        val input = UpdateRouteInput(
            id = "RT-001",
            originHub = originHub,
            destinationHub = destinationHub
        )

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Valid when only originHub is provided`() {
        val input = UpdateRouteInput(id = "RT-001", originHub = originHub)

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Valid when only destinationHub is provided`() {
        val input = UpdateRouteInput(id = "RT-001", destinationHub = destinationHub)

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with all violations when multiple fields are invalid`() {
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = -5.0,
            typicalDelayMin = -1,
            originHub = originHub,
            destinationHub = originHub
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).hasSize(3)
        assertThat(invalid.violations.any { it is InvalidRouteDistanceException }).isTrue()
        assertThat(invalid.violations.any { it is InvalidRouteDelayException }).isTrue()
        assertThat(invalid.violations.any { it is SameOriginAndDestinationException }).isTrue()
    }
}
