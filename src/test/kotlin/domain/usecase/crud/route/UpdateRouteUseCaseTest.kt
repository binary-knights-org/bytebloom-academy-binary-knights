package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import domain.model.exception.SameOriginAndDestinationException
import domain.model.input.UpdateRouteInput
import domain.model.validation.ValidationResult
import domain.repository.RouteRepository
import domain.validator.routes.RouteValidationError
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
        id = "WH-ORIGIN", name = "Origin Hub", regionalZone = RegionalZone.NORTH, latitude = 10.0, longitude = 20.0
    )

    private val destinationHub = Warehouse(
        id = "WH-DEST", name = "Destination Hub", regionalZone = RegionalZone.SOUTH, latitude = 30.0, longitude = 40.0
    )

    private val existingRoute = Route(
        id = "RT-001", distanceKm = 100.0, typicalDelayMin = 10, originHub = originHub, destinationHub = destinationHub
    )

    @Test
    fun `invoke when input is valid and existing route updated then returns Result success`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001", distanceKm = 150.0, typicalDelayMin = 20
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            routeRepository.getById("RT-001")
        } returns existingRoute

        coEvery {
            routeRepository.update(any())
        } returns true

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

        coVerify(exactly = 1) {
            routeRepository.getById("RT-001")
        }

        coVerify(exactly = 1) {
            routeRepository.update(any())
        }
    }

    @Test
    fun `invoke when validation fails then returns EntityValidationException without querying repo`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001", distanceKm = 0.0
        )

        val violations = listOf(
            RouteValidationError.InvalidDistance
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()

        val exception = result.exceptionOrNull()

        assertThat(exception is EntityValidationException)

        val validationException = exception as EntityValidationException

        assertThat(validationException.violations).containsExactly(RouteValidationError.InvalidDistance)

        coVerify(exactly = 0) {
            routeRepository.getById(any())
        }

        coVerify(exactly = 0) {
            routeRepository.update(any())
        }
    }

    @Test
    fun `invoke when route does not exist then returns Result failure with ResourceNotFoundException`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-NON-EXISTENT", distanceKm = 150.0
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            routeRepository.getById("RT-NON-EXISTENT")
        } returns null

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull() is ResourceNotFoundException)

        coVerify(exactly = 1) {
            routeRepository.getById("RT-NON-EXISTENT")
        }

        coVerify(exactly = 0) {
            routeRepository.update(any())
        }
    }

    @Test
    fun `invoke when retrieving route throws exception then returns the same exception`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001", distanceKm = 150.0
        )

        val exception = RuntimeException("Database error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            routeRepository.getById("RT-001")
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) {
            routeRepository.getById("RT-001")
        }

        coVerify(exactly = 0) {
            routeRepository.update(any())
        }
    }

    @Test
    fun `invoke when repo update returns false then returns Result failure with OperationFailedException`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001", distanceKm = 200.0
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            routeRepository.getById("RT-001")
        } returns existingRoute

        coEvery {
            routeRepository.update(any())
        } returns false

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull() is OperationFailedException)

        coVerify(exactly = 1) {
            routeRepository.update(any())
        }
    }

    @Test
    fun `invoke when repo update throws exception then returns the same exception`() = runTest {
        // Given
        val input = UpdateRouteInput(
            id = "RT-001", distanceKm = 200.0
        )

        val exception = RuntimeException("DB update error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            routeRepository.getById("RT-001")
        } returns existingRoute

        coEvery {
            routeRepository.update(any())
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) {
            routeRepository.update(any())
        }
    }

    @Test
    fun `invoke when updated route violates domain invariants then returns Result failure with domain exception`() =
        runTest {
            // Given - updating destinationHub to equal originHub
            val input = UpdateRouteInput(
                id = "RT-001", destinationHub = originHub
            )

            every {
                validator.validate(input)
            } returns ValidationResult.Valid

            coEvery {
                routeRepository.getById("RT-001")
            } returns existingRoute

            // When
            val result = useCase(input)

            // Then
            assertThat(result.isFailure).isTrue()

            assertThat(result.exceptionOrNull() is SameOriginAndDestinationException)

            coVerify(exactly = 1) {
                routeRepository.getById("RT-001")
            }

            coVerify(exactly = 0) {
                routeRepository.update(any())
            }
        }
}
