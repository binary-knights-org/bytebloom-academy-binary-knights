package domain.validator.routes

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidRouteDelayException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.CreateRouteInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test

class CreateRouteValidatorTest {

    private val validator = CreateRouteValidator()

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
    fun `should return Valid when all fields are valid`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 150.0,
            typicalDelayMin = 15,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with InvalidRouteDistanceException when distance is zero`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 0.0,
            typicalDelayMin = 10,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is InvalidRouteDistanceException }).isTrue()
    }

    @Test
    fun `should return Invalid with InvalidRouteDistanceException when distance is negative`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = -25.5,
            typicalDelayMin = 10,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is InvalidRouteDistanceException }).isTrue()
    }

    @Test
    fun `should return Valid when typicalDelayMin is zero`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 50.0,
            typicalDelayMin = 0,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val result = validator.validate(input)

        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `should return Invalid with InvalidRouteDelayException when typicalDelayMin is negative`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 50.0,
            typicalDelayMin = -5,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is InvalidRouteDelayException }).isTrue()
    }

    @Test
    fun `should return Invalid with SameOriginAndDestinationException when hubs have same id`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 100.0,
            typicalDelayMin = 10,
            originHub = originHub,
            destinationHub = originHub.copy()
        )

        val result = validator.validate(input)

        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations.any { it is SameOriginAndDestinationException }).isTrue()
    }

    @Test
    fun `should return Invalid with all violations when multiple fields are invalid`() {
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = -1.0,
            typicalDelayMin = -10,
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
