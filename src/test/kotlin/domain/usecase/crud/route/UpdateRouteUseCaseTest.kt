package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.ResourceNotFoundException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.UpdateRouteInput
import domain.model.validation.ValidationResult
import domain.repository.RouteRepository
import domain.validator.routes.UpdateRouteValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class UpdateRouteUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val validator = mockk<UpdateRouteValidator>()
    private val useCase = UpdateRouteUseCase(routeRepository, validator)

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

    private val existingRoute = Route(
        id = "RT-001",
        distanceKm = 100.0,
        typicalDelayMin = 10,
        originHub = originHub,
        destinationHub = destinationHub
    )

    private val updatedRoute = Route(
        id = "RT-001",
        distanceKm = 150.0,
        typicalDelayMin = 20,
        originHub = originHub,
        destinationHub = destinationHub
    )

    @Test
    fun `should update route successfully when input is valid`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = 150.0,
            typicalDelayMin = 20
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.getById("RT-001") } returns existingRoute
        coEvery { routeRepository.update(any()) } returns updatedRoute

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isSuccess).isTrue()
        val updated = result.getOrNull()
        assertThat(updated).isNotNull()
        assertThat(updated?.id).isEqualTo("RT-001")
        assertThat(updated?.distanceKm).isEqualTo(150.0)
        assertThat(updated?.typicalDelayMin).isEqualTo(20)

        coVerify(exactly = 1) { routeRepository.getById("RT-001") }
        coVerify(exactly = 1) { routeRepository.update(any()) }
    }

    @Test
    fun `should return EntityValidationException when validation fails`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = 0.0
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

        coVerify(exactly = 0) { routeRepository.getById(any()) }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should return ResourceNotFoundException when route does not exist`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-NON-EXISTENT",
            distanceKm = 150.0
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.getById("RT-NON-EXISTENT") } returns null

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(ResourceNotFoundException::class.java)

        coVerify(exactly = 1) { routeRepository.getById("RT-NON-EXISTENT") }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should return repository exception when fetching route fails`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = 150.0
        )

        val exception = RuntimeException("Database error")

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.getById("RT-001") } throws exception

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) { routeRepository.getById("RT-001") }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should return repository exception when updating route fails`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001",
            distanceKm = 200.0
        )

        val exception = RuntimeException("DB update error")

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.getById("RT-001") } returns existingRoute
        coEvery { routeRepository.update(any()) } throws exception

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) { routeRepository.update(any()) }
    }

    @Test
    fun `should return domain exception when updated route violates domain invariants`() = runTest {
        // Given - updating destinationHub to equal originHub
        val input = UpdateRouteInput(
            id = "RT-001",
            destinationHub = originHub
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { routeRepository.getById("RT-001") } returns existingRoute

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(SameOriginAndDestinationException::class.java)

        coVerify(exactly = 1) { routeRepository.getById("RT-001") }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }
}
