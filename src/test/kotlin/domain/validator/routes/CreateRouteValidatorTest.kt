package domain.validator.routes

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.CreateRouteInput
import domain.validator.ValidationResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateRouteValidatorTest {

    private lateinit var validator: CreateRouteValidator

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

    @BeforeEach
    fun setUp() {
        validator = CreateRouteValidator()
    }

    @Test
    fun `validate when all fields are valid then returns Valid`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 150.0,
            typicalDelayMin = 15,
            originHub = originHub,
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when distance is zero then returns Invalid with InvalidDistance`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 0.0,
            typicalDelayMin = 10,
            originHub = originHub,
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.InvalidDistance)
    }

    @Test
    fun `validate when distance is negative then returns Invalid with InvalidDistance`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = -25.5,
            typicalDelayMin = 10,
            originHub = originHub,
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.InvalidDistance)
    }

    @Test
    fun `validate when typicalDelayMin is zero then returns Valid`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 50.0,
            typicalDelayMin = 0,
            originHub = originHub,
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate when typicalDelayMin is negative then returns Invalid with NegativeDelay`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 50.0,
            typicalDelayMin = -5,
            originHub = originHub,
            destinationHub = destinationHub
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        val invalid = result as ValidationResult.Invalid
        assertThat(invalid.violations).containsExactly(RouteValidationError.NegativeDelay)
    }

    @Test
    fun `validate when origin and destination hubs have same id then returns Invalid with SameOriginAndDestination`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 100.0,
            typicalDelayMin = 10,
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
    fun `validate when multiple fields are invalid then returns Invalid with all violations`() {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = -1.0,
            typicalDelayMin = -10,
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
