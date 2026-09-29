package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import data.exception.NetworkUnavailableException
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import domain.model.exception.DataUnavailableException
import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.UpdateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.RouteValidationError
import domain.validator.routes.UpdateRouteValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateRouteUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var validator: UpdateRouteValidator
    private lateinit var useCase: UpdateRouteUseCase

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

    @BeforeEach
    fun setUp() {
        routeRepository = mockk()
        validator = mockk()
        useCase = UpdateRouteUseCase(routeRepository, validator)
    }

    @Test
    fun `invoke when input is valid and existing route updated then returns Result success`() =
        runBlocking {
            // Given
            val input = UpdateRouteInput(
                id = "RT-001",
                distanceKm = 150.0,
                typicalDelayMin = 20
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.getById("RT-001") } returns existingRoute
            coEvery { routeRepository.update(any()) } returns true

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isSuccess).isTrue()
            val updated = result.getOrNull()
            assertThat(updated).isNotNull()
            assertThat(updated?.id).isEqualTo("RT-001")
            assertThat(updated?.distanceKm).isEqualTo(150.0)
            assertThat(updated?.typicalDelayMin).isEqualTo(20)
            assertThat(updated?.originHub).isEqualTo(originHub)
            assertThat(updated?.destinationHub).isEqualTo(destinationHub)
            coVerify(exactly = 1) { routeRepository.getById("RT-001") }
            coVerify(exactly = 1) { routeRepository.update(any()) }
        }

    @Test
    fun `invoke when validation fails then returns EntityValidationException without querying repo`() =
        runBlocking {
            // Given
            val input = UpdateRouteInput(
                id = "RT-001",
                distanceKm = 0.0
            )
            val violations = listOf(RouteValidationError.InvalidDistance)
            every { validator.validate(input) } returns ValidationResult.Invalid(violations)

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            val exception = result.exceptionOrNull()
            assertThat(exception).isInstanceOf(EntityValidationException::class.java)
            val validationException = exception as EntityValidationException
            assertThat(validationException.violations).containsExactly(RouteValidationError.InvalidDistance)
            coVerify(exactly = 0) { routeRepository.getById(any()) }
            coVerify(exactly = 0) { routeRepository.update(any()) }
        }

    @Test
    fun `invoke when route does not exist then returns Result failure with ResourceNotFoundException`() =
        runBlocking {
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
    fun `invoke when retrieving route throws NetworkUnavailableException then returns DataUnavailableException`() =
        runBlocking {
            // Given
            val input = UpdateRouteInput(
                id = "RT-001",
                distanceKm = 150.0
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.getById("RT-001") } throws NetworkUnavailableException("No connection")

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(DataUnavailableException::class.java)
            coVerify(exactly = 1) { routeRepository.getById("RT-001") }
            coVerify(exactly = 0) { routeRepository.update(any()) }
        }

    @Test
    fun `invoke when repo update returns false then returns Result failure with OperationFailedException`() =
        runBlocking {
            // Given
            val input = UpdateRouteInput(
                id = "RT-001",
                distanceKm = 200.0
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.getById("RT-001") } returns existingRoute
            coEvery { routeRepository.update(any()) } returns false

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.update(any()) }
        }

    @Test
    fun `invoke when repo update throws NetworkUnavailableException then returns DataUnavailableException`() =
        runBlocking {
            // Given
            val input = UpdateRouteInput(
                id = "RT-001",
                distanceKm = 200.0
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.getById("RT-001") } returns existingRoute
            coEvery { routeRepository.update(any()) } throws NetworkUnavailableException("Network failed")

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(DataUnavailableException::class.java)
            coVerify(exactly = 1) { routeRepository.update(any()) }
        }

    @Test
    fun `invoke when repo update throws generic exception then returns translated OperationFailedException`() =
        runBlocking {
            // Given
            val input = UpdateRouteInput(
                id = "RT-001",
                distanceKm = 200.0
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.getById("RT-001") } returns existingRoute
            coEvery { routeRepository.update(any()) } throws RuntimeException("DB update error")

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.update(any()) }
        }

    @Test
    fun `invoke when updated route violates domain invariants then returns Result failure with domain exception`() =
        runBlocking {
            // Given - updating destinationHub to equal originHub (causing SameOriginAndDestinationException)
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
            coVerify(exactly = 0) { routeRepository.update(any()) }
        }
}
