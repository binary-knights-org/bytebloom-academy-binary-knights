package domain.usecase.crud.warehouse

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdateWarehouseInput
import domain.model.validation.ValidationResult
import domain.repository.WarehouseRepository
import domain.validator.warehouse.UpdateWarehouseValidator
import domain.validator.warehouse.WarehouseValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdateWarehouseUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val validator = mockk<UpdateWarehouseValidator>()

    private val useCase = UpdateWarehouseUseCase(
        warehouseRepository = warehouseRepository,
        validator = validator
    )

    private val existingWarehouse = Warehouse(
        id = "WH-001",
        name = "Hub-001",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 37.91,
        longitude = -88.46
    )

    private val validInput = UpdateWarehouseInput(
        id = "WH-001",
        name = "Updated Hub",
        regionalZone = "WEST",
        latitude = 40.0,
        longitude = -90.0
    )

    private val updatedWarehouse = Warehouse(
        id = "WH-001",
        name = "Updated Hub",
        regionalZone = RegionalZone.WEST,
        latitude = 40.0,
        longitude = -90.0
    )

    @Test
    fun `should update warehouse successfully for valid input`() = runTest {
        // Given
        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.getById(validInput.id)
        } returns existingWarehouse

        coEvery {
            warehouseRepository.update(updatedWarehouse)
        } returns true

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedWarehouse, result.getOrNull())

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.getById(validInput.id)
            warehouseRepository.update(updatedWarehouse)
        }
    }

    @Test
    fun `should return entity validation failure when input is invalid`() = runTest {
        // Given
        val violations = listOf(
            WarehouseValidationError.BlankName
        )

        every {
            validator.validate(validInput)
        } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is EntityValidationException
        )

        coVerify(exactly = 1) {
            validator.validate(validInput)
        }

        coVerify(exactly = 0) {
            warehouseRepository.getById(any())
            warehouseRepository.update(any())
        }
    }

    @Test
    fun `should return resource not found failure when warehouse does not exist`() = runTest {
        // Given
        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.getById(validInput.id)
        } returns null

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ResourceNotFoundException
        )

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.getById(validInput.id)
        }

        coVerify(exactly = 0) {
            warehouseRepository.update(any())
        }
    }

    @Test
    fun `should return failure when regional zone is invalid`() = runTest {
        // Given
        val input = validInput.copy(
            regionalZone = "INVALID_ZONE"
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.getById(input.id)
        } returns existingWarehouse

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is IllegalArgumentException
        )

        coVerify(exactly = 1) {
            validator.validate(input)
            warehouseRepository.getById(input.id)
        }

        coVerify(exactly = 0) {
            warehouseRepository.update(any())
        }
    }

    @Test
    fun `should return operation failed exception when repository update returns false`() = runTest {
        // Given
        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.getById(validInput.id)
        } returns existingWarehouse

        coEvery {
            warehouseRepository.update(updatedWarehouse)
        } returns false

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.getById(validInput.id)
            warehouseRepository.update(updatedWarehouse)
        }
    }

    @Test
    fun `should return repository exception when update fails`() = runTest {
        // Given
        val exception = RuntimeException("Database error")

        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.getById(validInput.id)
        } returns existingWarehouse

        coEvery {
            warehouseRepository.update(updatedWarehouse)
        } throws exception

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.getById(validInput.id)
            warehouseRepository.update(updatedWarehouse)
        }
    }
}
