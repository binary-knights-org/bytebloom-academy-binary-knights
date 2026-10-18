package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.input.CreateRouteInput
import domain.model.validation.ValidationResult
import domain.repository.RouteRepository
import domain.validator.routes.CreateRouteValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CreateRouteUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val validator = mockk<CreateRouteValidator>()
    private val useCase = CreateRouteUseCase(routeRepository, validator)

    private val originHub = Warehouse(
        id = "WH-ORIGIN", name = "Origin Hub", regionalZone = RegionalZone.NORTH, latitude = 10.0, longitude = 20.0
    )

    private val destinationHub = Warehouse(
        id = "WH-DEST", name = "Destination Hub", regionalZone = RegionalZone.SOUTH, latitude = 30.0, longitude = 40.0
    )

    private val createdRoute = Route(
        id = "RT-001",
        distanceKm = 120.0,
        typicalDelayMin = 15,
        originHub = originHub,
        destinationHub = destinationHub
    )

    @Test
    fun `invoke when input is valid and repo creates route then returns Result success`() = runTest {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 120.0,
            typicalDelayMin = 15,
            originHub = originHub,
            destinationHub = destinationHub
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.create(any()) } returns createdRoute

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isSuccess).isTrue()
        val route = result.getOrNull()
        assertThat(route).isNotNull()
        assertThat(route?.id).isEqualTo("RT-001")
        assertThat(route?.distanceKm).isEqualTo(120.0)
        assertThat(route?.typicalDelayMin).isEqualTo(15)
        assertThat(route?.originHub).isEqualTo(originHub)
        assertThat(route?.destinationHub).isEqualTo(destinationHub)

        coVerify(exactly = 1) { routeRepository.create(any()) }
    }

    @Test
    fun `invoke when validation fails then returns Result failure with EntityValidationException`() = runTest {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 0.0,
            typicalDelayMin = 10,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val violations = listOf(
            InvalidRouteDistanceException("Distance must be greater than zero")
        )

        every { validator.validate(input) } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(EntityValidationException::class.java)

        val validationException = exception as EntityValidationException
        assertThat(validationException.violations).containsExactlyElementsIn(violations)

        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `invoke when repo throws exception then returns the same exception`() = runTest {
        // Given
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = 100.0,
            typicalDelayMin = 5,
            originHub = originHub,
            destinationHub = destinationHub
        )

        val exception = RuntimeException("Database error")

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.create(any()) } throws exception

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) { routeRepository.create(any()) }
    }

    @Test
    fun `invoke when route model creation throws exception then returns Result failure with exception`() = runTest {
        // Given - validator improperly passes invalid distance
        val input = CreateRouteInput(
            id = "RT-001",
            distanceKm = -10.0,
            typicalDelayMin = 5,
            originHub = originHub,
            destinationHub = destinationHub
        )

        every { validator.validate(input) } returns ValidationResult.Valid

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidRouteDistanceException::class.java)

        coVerify(exactly = 0) { routeRepository.create(any()) }
    }
}
