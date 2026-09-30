package domain.usecase.crud.vehicle

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.InvalidVehicleCapacityException
import domain.model.exception.OperationFailedException
import domain.model.input.CreateVehicleInput
import domain.model.validation.ValidationResult
import domain.repository.VehicleRepository
import domain.validator.vehicle.CreateVehicleValidator
import domain.validator.vehicle.VehicleValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val validator = mockk<CreateVehicleValidator>()
    private val useCase = CreateVehicleUseCase(vehicleRepository, validator)

    private val origin = Warehouse(
        id = "WH-1", name = "Origin Warehouse", regionalZone = RegionalZone.NORTH,
        latitude = 31.95, longitude = 35.91
    )

    @Test
    fun `should create vehicle successfully`() = runTest {
        // Given
        val input = CreateVehicleInput(
            id = "TRK-1", currentHub = origin, maxCapacityKg = 7500.0, costPerKm = 5.0
        )
        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { vehicleRepository.create(any()) } returns true

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("TRK-1", result.getOrNull()?.id)
        assertEquals(7500.0, result.getOrNull()?.maxCapacityKg)
        assertEquals(origin, result.getOrNull()?.currentHub)

        coVerify(exactly = 1) { vehicleRepository.create(any()) }
    }

    @Test
    fun `should return EntityValidationException when validation fails`() = runTest {
        // Given
        val input = CreateVehicleInput(
            id = "TRK-1", currentHub = origin, maxCapacityKg = 0.0, costPerKm = 5.0
        )
        val violations = listOf(VehicleValidationError.InvalidCapacity)
        every { validator.validate(input) } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is EntityValidationException)
        assertEquals(violations, exception.violations)

        coVerify(exactly = 1) { validator.validate(input) }
        coVerify(exactly = 0) { vehicleRepository.create(any()) }
    }

    @Test
    fun `should return OperationFailedException when repository creation returns false`() = runTest {
        // Given
        val input = CreateVehicleInput(
            id = "TRK-1", currentHub = origin, maxCapacityKg = 7500.0, costPerKm = 5.0
        )
        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { vehicleRepository.create(any()) } returns false

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is OperationFailedException)

        coVerify(exactly = 1) { vehicleRepository.create(any()) }
    }

    @Test
    fun `should return repository exception when repository creation throws`() = runTest {
        // Given
        val input = CreateVehicleInput(
            id = "TRK-1", currentHub = origin, maxCapacityKg = 7500.0, costPerKm = 5.0
        )
        val exception = RuntimeException("Database error")
        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { vehicleRepository.create(any()) } throws exception

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) { vehicleRepository.create(any()) }
    }

    @Test
    fun `should return vehicle model exception when validator incorrectly allows invalid capacity`() = runTest {
        // Given
        val input = CreateVehicleInput(
            id = "TRK-1", currentHub = origin, maxCapacityKg = 0.0, costPerKm = 5.0
        )
        every { validator.validate(input) } returns ValidationResult.Valid

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidVehicleCapacityException)

        coVerify(exactly = 0) { vehicleRepository.create(any()) }
    }
}
