package domain.usecase.crud.vehicle

import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdateVehicleInput
import domain.model.validation.ValidationResult
import domain.repository.VehicleRepository
import domain.validator.vehicle.UpdateVehicleValidator
import domain.validator.vehicle.VehicleValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val validator = mockk<UpdateVehicleValidator>()

    private val useCase = UpdateVehicleUseCase(
         vehicleRepository,
         validator
    )

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )


    private val existingPackage = Vehicle(
        id = "TRK-1",
        currentHub = origin,
        maxCapacityKg = 7900.0,
        costPerKm = 4.2
        )


    @Test
    fun `should update vehicle successfully`() = runTest {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            costPerKm = 1.8
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            vehicleRepository.getById("TRK-1")
        } returns existingPackage

        coEvery {
            vehicleRepository.update(any())
        } returns true

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1.8, result.getOrNull()?.costPerKm)

        coVerify(exactly = 1) {
            vehicleRepository.getById("TRK-1")
            vehicleRepository.update(any())
        }
    }

    @Test
    fun `should return EntityValidationException when validation fails`() = runTest {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1"
        )

        val violations = listOf(
            VehicleValidationError.NoUpdateFields
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue(exception is EntityValidationException)

        val validationException = exception as EntityValidationException
        assertEquals(violations, validationException.violations)

        coVerify(exactly = 1) {
            validator.validate(input)
        }

        coVerify(exactly = 0) {
            vehicleRepository.getById(any())
            vehicleRepository.update(any())
        }
    }


    @Test
    fun `should return ResourceNotFoundException when vehicle does not exist`() = runTest {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-999",
            costPerKm = 4.2
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            vehicleRepository.getById("TRK-999")
        } returns null

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ResourceNotFoundException
        )

        coVerify(exactly = 1) {
            vehicleRepository.getById("TRK-999")
        }

        coVerify(exactly = 0) {
            vehicleRepository.update(any())
        }
    }

    @Test
    fun `should return OperationFailedException when update returns false`() = runTest {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            costPerKm = 4.5
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            vehicleRepository.getById("TRK-1")
        } returns existingPackage

        coEvery {
            vehicleRepository.update(any())
        } returns false

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            vehicleRepository.update(any())
        }
    }

    @Test
    fun `should return repository exception when fetching package fails`() = runTest {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            costPerKm = 4.0
        )

        val exception = RuntimeException("Database error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            vehicleRepository.getById("TRK-1")
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            vehicleRepository.getById("TRK-1")
        }

        coVerify(exactly = 0) {
            vehicleRepository.update(any())
        }
    }


    @Test
    fun `should return repository exception when updating package fails`() = runTest {
        // Given
        val input = UpdateVehicleInput(
            id = "TRK-1",
            costPerKm = 4.0
        )

        val exception = RuntimeException("Database error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            vehicleRepository.getById("TRK-1")
        } returns existingPackage

        coEvery {
            vehicleRepository.update(any())
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            vehicleRepository.update(any())
        }
    }
}
