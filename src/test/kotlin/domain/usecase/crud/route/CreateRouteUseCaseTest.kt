package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import data.exception.NetworkUnavailableException
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.DataUnavailableException
import domain.model.exception.EntityValidationException
import domain.model.exception.InvalidRouteDistanceException
import domain.model.exception.OperationFailedException
import domain.model.input.CreateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.CreateRouteValidator
import domain.validator.routes.RouteValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateRouteUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var validator: CreateRouteValidator
    private lateinit var useCase: CreateRouteUseCase

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
        routeRepository = mockk()
        validator = mockk()
        useCase = CreateRouteUseCase(routeRepository, validator)
    }

    @Test
    fun `invoke when input is valid and repo creates route then returns Result success`() =
        runBlocking {
            // Given
            val input = CreateRouteInput(
                id = "RT-001",
                distanceKm = 120.0,
                typicalDelayMin = 15,
                originHub = originHub,
                destinationHub = destinationHub
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.create(any()) } returns true

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isSuccess).isTrue()
            val createdRoute = result.getOrNull()
            assertThat(createdRoute).isNotNull()
            assertThat(createdRoute?.id).isEqualTo("RT-001")
            assertThat(createdRoute?.distanceKm).isEqualTo(120.0)
            assertThat(createdRoute?.typicalDelayMin).isEqualTo(15)
            assertThat(createdRoute?.originHub).isEqualTo(originHub)
            assertThat(createdRoute?.destinationHub).isEqualTo(destinationHub)
            coVerify(exactly = 1) { routeRepository.create(any()) }
        }

    @Test
    fun `invoke when validation fails then returns Result failure with EntityValidationException`() =
        runBlocking {
            // Given
            val input = CreateRouteInput(
                id = "RT-001",
                distanceKm = 0.0,
                typicalDelayMin = 10,
                originHub = originHub,
                destinationHub = destinationHub
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
            coVerify(exactly = 0) { routeRepository.create(any()) }
        }

    @Test
    fun `invoke when repo returns false then returns Result failure with OperationFailedException`() =
        runBlocking {
            // Given
            val input = CreateRouteInput(
                id = "RT-001",
                distanceKm = 100.0,
                typicalDelayMin = 5,
                originHub = originHub,
                destinationHub = destinationHub
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.create(any()) } returns false

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.create(any()) }
        }

    @Test
    fun `invoke when repo throws NetworkUnavailableException then returns DataUnavailableException`() =
        runBlocking {
            // Given
            val input = CreateRouteInput(
                id = "RT-001",
                distanceKm = 100.0,
                typicalDelayMin = 5,
                originHub = originHub,
                destinationHub = destinationHub
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.create(any()) } throws NetworkUnavailableException("No connection")

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(DataUnavailableException::class.java)
            coVerify(exactly = 1) { routeRepository.create(any()) }
        }

    @Test
    fun `invoke when repo throws generic exception then returns translated OperationFailedException`() =
        runBlocking {
            // Given
            val input = CreateRouteInput(
                id = "RT-001",
                distanceKm = 100.0,
                typicalDelayMin = 5,
                originHub = originHub,
                destinationHub = destinationHub
            )
            every { validator.validate(input) } returns ValidationResult.Valid
            coEvery { routeRepository.create(any()) } throws RuntimeException("Database error")

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.create(any()) }
        }

    @Test
    fun `invoke when route model creation throws exception then returns Result failure with exception`() =
        runBlocking {
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
