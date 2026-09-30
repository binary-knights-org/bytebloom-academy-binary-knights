package domain.validator.routes

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.UpdateRouteInput
import domain.model.validation.ValidationResult
import org.junit.jupiter.api.Test

class UpdateRouteValidatorTest {

    private var validator: UpdateRouteValidator = UpdateRouteValidator()

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
    fun `validate when no update fields are provided then returns Invalid with NoUpdateFields`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = null,
            typicalDelayMin = null,
            originHub = null,
            destinationHub = null
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.NoUpdateFields)
    }

    @Test
    fun `validate when only valid distanceKm is provided then returns Valid`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = 120.5
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when only valid typicalDelayMin is provided then returns Valid`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            typicalDelayMin = 25
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when typicalDelayMin is zero then returns Valid`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            typicalDelayMin = 0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when distanceKm is zero then returns Invalid with InvalidDistance`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = 0.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.InvalidDistance)
    }

    @Test
    fun `validate when distanceKm is negative then returns Invalid with InvalidDistance`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = -15.0
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.InvalidDistance)
    }

    @Test
    fun `validate when typicalDelayMin is negative then returns Invalid with NegativeDelay`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            typicalDelayMin = -3
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.NegativeDelay)
    }

    @Test
    fun `validate when both hubs are provided with same id then returns Invalid with SameOriginAndDestination`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            originHub = originHub,
            destinationHub = originHub.copy()
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.SameOriginAndDestination)
    }

    @Test
    fun `validate when both hubs are provided with different ids then returns Valid`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            originHub = originHub,
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when only originHub is provided then returns Valid`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            originHub = originHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when only destinationHub is provided then returns Valid`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when multiple fields are invalid then returns Invalid with all violations`() {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = -5.0,
            typicalDelayMin = -1,
            originHub = originHub,
            destinationHub = originHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(
            RouteValidationError.InvalidDistance,
            RouteValidationError.NegativeDelay,
            RouteValidationError.SameOriginAndDestination
        )
    }
}
